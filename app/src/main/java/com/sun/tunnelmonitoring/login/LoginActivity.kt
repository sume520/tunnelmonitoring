package com.sun.tunnelmonitoring.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.sun.tunnelmonitoring.MainActivity
import com.sun.tunnelmonitoring.MyApplication
import com.sun.tunnelmonitoring.R
import com.sun.tunnelmonitoring.utils.SharedPreferencesUtils

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isLogin= SharedPreferencesUtils.getLoginStatus(MyApplication.getContext())
        val isAutoLogin= SharedPreferencesUtils.get_flag_auto(MyApplication.getContext())
        if(isLogin&&isAutoLogin){
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        setContentView(R.layout.activity_login)
        supportFragmentManager.inTransaction { replace(R.id.loginacivity_fragment,LoginFragment.newInstance()) }
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }
}


