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
import com.google.gson.Gson
import com.sun.tunnelmonitoring.MyApplication
import com.sun.tunnelmonitoring.db.manager.User
import com.sun.tunnelmonitoring.db.manager.UserDao
import kotlinx.android.synthetic.main.tree_item.*


class LoginFragment : Fragment() {
    private var loginobject: JSONObject? = null
    private var loginjsonString: String? = null
    private val URL = "http://47.107.158.26:80/user/applogin"
    private var account=""
    private var password=""

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

        //设置密码是否可见
        tb_show_hide_pass.setOnCheckedChangeListener{ compoundButton, isChecked ->
            if (isChecked) {
                et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }
        //var context=activity!!.applicationContext
        //判断是否自动登录
        if (SharedPreferencesUtils.get_flag_auto(context!!)) {
            val account = SharedPreferencesUtils.getaccount(context!!)
            val password = SharedPreferencesUtils.getpswd(context!!)
            postRequest(account!!, password!!)
        }
        //判断是否记住密码
        if (SharedPreferencesUtils.get_flag_rem(context!!)) {
            val account = SharedPreferencesUtils.getaccount(context!!)
            val password = SharedPreferencesUtils.getpswd(context!!)
            et_account.setText(account)
            et_password.setText(password)
            cb_remember_pass.isChecked = true
        }

        cb_autologin.setOnCheckedChangeListener { buttonView, isChecked ->
            cb_remember_pass.isChecked = isChecked
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
            val intent=Intent(context,LoginActivity::class.java)
            intent.putExtra("param","register")
            startActivity(intent)
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
            var response: Response? = null
            try {
                //回调
                response = client.newCall(request).execute()
                if (response!!.isSuccessful) {
                    val json=response.body().string().trim()
                    Log.i("postRequest","获取到数据: $json")
                    //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                    handler.obtainMessage(1, json).sendToTarget()
                } else {
                    Log.e("postRequest","登录出错")
                    handler.post { Toast.makeText(context,"登录失败！！",Toast.LENGTH_SHORT).show() }
                    throw IOException("Unexpected code:" + response!!)
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
                    Log.i("xxxaccount", SharedPreferencesUtils.getaccount(context!!) + "")
                    if (cb_autologin.isChecked()) {
                        SharedPreferencesUtils.set_flag_auto(true, context!!)
                    } else {
                        SharedPreferencesUtils.set_flag_auto(false, context!!)
                    }
                    //保存账号密码
                    if (cb_remember_pass.isChecked()) {
                        SharedPreferencesUtils.setaccount(account, context!!)
                        SharedPreferencesUtils.setpswd(password, context!!)
                        SharedPreferencesUtils.set_flag_rem(true, context!!)
                    }
                    val user = Gson().fromJson(ReturnMessage, User::class.java)
                    if(UserDao.queryById(1)!=null)
                        user.update(1)
                    else
                        user.save()
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    activity!!.finish()//关闭页面
                }
            }
        }
    }
}
