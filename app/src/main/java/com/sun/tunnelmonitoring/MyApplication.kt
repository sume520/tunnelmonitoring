package com.sun.tunnelmonitoring

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        private var context: Context? = null
        fun getContext() = context!!
    }

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
    }
}