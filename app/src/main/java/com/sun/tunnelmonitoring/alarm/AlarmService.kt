package com.sun.tunnelmonitoring.alarm

import android.app.IntentService
import android.content.Context
import android.content.Intent
import java.net.Socket

object AlarmService : IntentService("AlarmService") {

    private var flag = false
    private lateinit var client: Socket

    init {

    }

    override fun onHandleIntent(intent: Intent?) {
        flag = true

        while (flag) {
            //TODO 循环监听后台警报
        }

    }

    fun startService(context: Context) {
        val intent = Intent(context, AlarmService::class.java)
        context.startService(intent)
    }

    //关闭后台
    fun close() {
        flag = false
    }


}
