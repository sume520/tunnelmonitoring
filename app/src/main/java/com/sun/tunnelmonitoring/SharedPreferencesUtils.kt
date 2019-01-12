package com.sun.tunnelmonitoring

import android.content.Context
import android.content.SharedPreferences


object SharedPreferencesUtils {
    fun share(context: Context): SharedPreferences {
        return context.getSharedPreferences("Login", Context.MODE_PRIVATE)
    }

    fun getLoginStatus(context: Context):Boolean{
        return share(context)
            .getBoolean("login",false)
    }

    fun setLoginStatus(isLogin:Boolean,context: Context){
        val edit=share(context).edit()
        edit.putBoolean("login",isLogin).apply()
    }

    fun getaccount(context: Context): String? {
        return share(context)
            .getString("account", null)
    }

    fun setaccount(account: String, context: Context) {
        val e = share(context).edit()
        e.putString("account", account)
        e.apply()
    }

    fun getpswd(context: Context): String? {
        return share(context)
            .getString("password", null)
    }

    fun setpswd(pswd: String, context: Context) {
        val e = share(context).edit()
        e.putString("password", pswd)
        e.apply()
    }

    fun get_flag_rem(context: Context): Boolean {
        return share(context)
            .getBoolean("flag_rem", false)
    }

    fun set_flag_rem(flag_rem: Boolean?, context: Context) {
        val e = share(context).edit()
        e.putBoolean("flag_rem", flag_rem!!)
        e.apply()
    }

    fun get_flag_auto(context: Context): Boolean {
        return share(context)
            .getBoolean("flag_auto", false)
    }

    fun set_flag_auto(flag_auto: Boolean?, context: Context) {
        val e = share(context).edit()
        e.putBoolean("flag_auto", flag_auto!!)
        e.apply()
    }

}
