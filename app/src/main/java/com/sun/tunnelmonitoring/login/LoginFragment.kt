package com.sun.tunnelmonitoring.login


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.okhttp.*
import com.sun.tunnelmonitoring.MainActivity
import com.sun.tunnelmonitoring.R
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import com.squareup.okhttp.RequestBody
import android.os.Message
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import com.sun.tunnelmonitoring.Utils.SharedPreferencesUtils


class LoginFragment : Fragment() {
    private var loginobject: JSONObject? = null
    private var loginjsonString: String? = null
    private val URL = "http://47.107.158.26:80/user/applogin"
    private var account=""
    private var password=""
    private val ctx=MyApplication.getContext()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
                LoginFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.title="登录"
        cb_autologin.isChecked=false
        SharedPreferencesUtils.set_flag_auto(false,ctx)

        //设置密码是否可见
        tb_show_hide_pass.setOnCheckedChangeListener{ _, isChecked ->
            if (!isChecked) {
                et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }

        //判断是否自动登录
        if (SharedPreferencesUtils.get_flag_auto(ctx)) {
            val account = SharedPreferencesUtils.getaccount(ctx)
            val password = SharedPreferencesUtils.getpswd(ctx)
            postRequest(account!!, password!!)
        }

        //勾选自动登录自动勾选记住密码
        cb_autologin.setOnCheckedChangeListener { _, isChecked ->
            cb_remember_pass.isChecked = isChecked
        }

        //判断是否记住密码
        if (SharedPreferencesUtils.get_flag_rem(ctx)) {
            val account = SharedPreferencesUtils.getaccount(ctx)
            val password = SharedPreferencesUtils.getpswd(ctx)
            et_account.setText(account)
            et_password.setText(password)
            cb_remember_pass.isChecked = true
        }

        //登录
        bt_login.setOnClickListener{
            account=et_account.text.toString().trim()
            password=et_password.text.toString().trim()
            Log.i("login","account: $account, password: $password")
            postRequest(account,password)
        }
        //注册
        bt_register.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction().add(R.id.loginacivity_fragment, RegisterFragment.newInstance())
                .addToBackStack(null)
                .commit()

        }

    }

    private val client=OkHttpClient()

    //请求数据
    private fun postRequest(account: String, password: String) {
        //建立请求表单，添加上传服务器的参数
        loginobject = null
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
        //新建一个线程，用于得到服务器响应的参数
        Thread {
            var response: Response
            try {
                //回调
                response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val json=response.body().string().trim()
                    Log.i("postRequest","获取到数据: $json")
                    //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                    handler.obtainMessage(1, json).sendToTarget()
                } else {
                    Log.e("postRequest","登录出错")
                    handler.post { Toast.makeText(context,"登录失败！！",Toast.LENGTH_SHORT).show() }
                    throw IOException("Unexpected code:" + response)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                val ReturnMessage = msg.obj as String
                if (ReturnMessage == "10") {
                    Toast.makeText(MyApplication.getContext(), "密码错误或账户未激活", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i("xxxaccount", SharedPreferencesUtils.getaccount(ctx) + "")
                    if (cb_autologin.isChecked()) {//是否自动登录
                        SharedPreferencesUtils.set_flag_auto(true, ctx)
                    } else {
                        SharedPreferencesUtils.set_flag_auto(false,ctx)
                    }

                    if (cb_remember_pass.isChecked()) {//是否保存账号密码
                        SharedPreferencesUtils.setaccount(account, ctx)
                        SharedPreferencesUtils.setpswd(password, ctx)
                        SharedPreferencesUtils.set_flag_rem(true, ctx)
                    }else{
                        SharedPreferencesUtils.set_flag_rem(false,ctx)
                    }

                    SharedPreferencesUtils.setLoginStatus(true,ctx)
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    activity!!.finish()//关闭页面
                }
            }
        }
    }
}
