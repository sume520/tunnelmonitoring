package com.sun.tunnelmonitoring.login


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
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
    //private var sharePref=PreferenceManager.getDefaultSharedPreferences(activity)
    private var editor: SharedPreferences.Editor? = null
    private var loginobject: JSONObject? = null
    private var loginjsonString: String? = null
    private var response: String = ""
    private var json:String = ""
    private var message=""
    private val URL = "http://192.168.43.129:1234/user/applogin"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
                LoginFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPref=activity!!.getSharedPreferences("userdata",Context.MODE_PRIVATE)
        val isRemember=sharedPref.getBoolean("remember_password",false)
        val isauto=sharedPref.getBoolean("auto_login",false)
        tb_show_hide_pass.setOnCheckedChangeListener{ compoundButton, isChecked ->
            if (isChecked) {
                tb_show_hide_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            } else {
                tb_show_hide_pass.setTransformationMethod(PasswordTransformationMethod.getInstance())
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
            cb_remember_pass!!.setChecked(true)
        }
        bt_login!!.setOnClickListener{
            val account = et_account!!.getText().toString()
            val password = et_password!!.getText().toString()

            object : Thread() {
                override fun run() {
                    json = postJson(account, password)
                }
            }.start()
            if (json == "1") {
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
                val intent2 = Intent(activity, MainActivity::class.java)
                startActivity(intent2)
            } else {
                Toast.makeText(activity, "用户名或密码错误", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun postJson(account: String, password: String): String {
        val loginclient = OkHttpClient()
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
                .url("http://192.168.43.129:1234/user/applogin")
                .post(body)
                .build()
        val call = loginclient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(request: Request, e: IOException) {
                e.printStackTrace()
            }
            @Throws(IOException::class)
            override fun onResponse(response: Response) {
                val msg = handler.obtainMessage()
                msg.obj = response.body().string()
                handler.sendMessage(msg)
            }
        })
        return response
    }

    internal var handler = Handler(Handler.Callback { msg ->
        message = msg.obj as String
        try {
            message = URLDecoder.decode(message, "utf-8")
            val jsonObject = JSONObject(message)
            response = jsonObject.getString("statas")
            Log.d("xxxxxxxxxxxxxxxxxx", response)
            if (response != "1") {
                Toast.makeText(activity, response, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@Callback false
    })
}
