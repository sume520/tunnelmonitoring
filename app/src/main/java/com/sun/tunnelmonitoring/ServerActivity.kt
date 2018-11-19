package com.sun.tunnelmonitoring

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_server.*
import java.lang.Exception

class ServerActivity : AppCompatActivity() {
    private lateinit var wifiManager:WifiManager
    private var flag=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)

    }

    private fun init() {
        //获取wifi管理服务
        wifiManager=getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private fun isAPOpened(): Boolean {
        try {
            var method=wifiManager.javaClass.getMethod("isWifiApEnabled")
            method.isAccessible=true
            Toast.makeText(this,"已开启热点：“TEXTAP”",Toast.LENGTH_SHORT).show()
            return method.invoke(wifiManager) as Boolean
        }catch (e:NoSuchMethodException){
            e.printStackTrace()
        }catch (e:Exception){
            e.printStackTrace()
            Toast.makeText(this,"已开启热点：“TEXTAP”",Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(this,"未开启热点：“TEXTAP”，请按要求开启",Toast.LENGTH_SHORT).show()
        return false
    }

    private inner class APReceiver:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            var action= intent!!.action
            if("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)){

            }
        }

    }

}