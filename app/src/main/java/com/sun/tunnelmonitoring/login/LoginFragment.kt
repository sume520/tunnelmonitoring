package com.sun.tunnelmonitoring.login


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.okhttp.*
import com.sun.tunnelmonitoring.MainActivity
import com.sun.tunnelmonitoring.MyApplication

import com.sun.tunnelmonitoring.R
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URLDecoder

class LoginFragment : Fragment() {
    private lateinit var sharedPref:SharedPreferences
    private var editor: SharedPreferences.Editor? = null
    private var loginobject: JSONObject? = null
    private var loginjsonString: String? = null
    private var response: String = ""
    private var message=""
    private val URL = "http://192.168.43.129:1234/user/applogin"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
                LoginFragment()
    }

    @SuppressLint("CommitPrefEdits")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPref=PreferenceManager.getDefaultSharedPreferences(context)

        val isRemember=sharedPref.getBoolean("remember_password",false)
        val isauto=sharedPref.getBoolean("auto_login",false)
        tb_show_hide_pass.setOnCheckedChangeListener{ compoundButton, isChecked ->
            if (isChecked) {
                tb_show_hide_pass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                tb_show_hide_pass.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        if (isauto) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
        if (isRemember) {
            val account=sharedPref.getString("account","")
            val password=sharedPref.getString("password","")
            et_account!!.setText(account)
            et_password!!.setText(password)
            cb_remember_pass!!.isChecked = true
        }
        bt_login!!.setOnClickListener{
            val account = et_account!!.text.toString()
            val password = et_password!!.text.toString()
            Thread{
                postJson(account, password)
                if (response == "1")
                {
                    editor = sharedPref.edit()
                    if (cb_remember_pass!!.isChecked()) {
                        editor!!.putBoolean("remember_password", true)
                        editor!!.putString("account", account)
                        editor!!.putString("password", password)
                        if (cb_autologin.isChecked()) {
                            editor!!.putBoolean("auto_login", true)
                        } else {
                            editor!!.putBoolean("auto_login", false)
                        }
                    } else {
                        editor!!.clear()
                    }
                        editor!!.apply()
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    handler.post{
                        Toast.makeText(activity,"登录成功",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    handler.post{
                        Toast.makeText(activity, "登录失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }

        bt_register.setOnClickListener {
            val intent=Intent(context,LoginActivity::class.java)
            intent.putExtra("param","register")
            startActivity(intent)
        }

    }
    private fun postJson(account: String, password: String) {
        val client = OkHttpClient()
        loginobject = JSONObject()
        try {
            loginobject!!.put("username", account)
            loginobject!!.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        loginjsonString = null
        loginjsonString = loginobject.toString()
        val body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), loginjsonString)
        val request = Request.Builder()
                .url(URL)
                .post(body)
                .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(request: Request, e: IOException) {
                e.printStackTrace()
                Log.e("okHttp","发送失败")
            }
            @Throws(IOException::class)
            override fun onResponse(response: Response) {
                val msg = handler.obtainMessage()
                msg.obj = response.body().string()
                handler.sendMessage(msg)
                Log.d("okHttp",msg.obj.toString())
            }
        })
        return
    }

    internal var handler = Handler(Handler.Callback { msg ->
        message = msg.obj as String
        try {
            message = URLDecoder.decode(message, "utf-8")
            val jsonObject = JSONObject(message)
            response = jsonObject.getString("statas")
            Log.d("handler", response)
            if (!"1".equals(response)) {
                Toast.makeText(activity, response, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@Callback false
    })
}
