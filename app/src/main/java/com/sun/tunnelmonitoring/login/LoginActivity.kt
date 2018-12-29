package com.sun.tunnelmonitoring.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.sun.tunnelmonitoring.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val param=intent.getStringExtra("param")
        when(param){
            "login"->{
                supportFragmentManager
                    .inTransaction { replace(R.id.loginacivity_fragment,LoginFragment.newInstance()) }
                title="登录"
            }
        }
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }
}


