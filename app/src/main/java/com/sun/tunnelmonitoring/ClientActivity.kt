package com.sun.tunnelmonitoring

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast

class ClientActivity : AppCompatActivity() {

    private var wifiList: List<ScanResult>? = null
    private var wifiReceiver: WifiReceiver? = null
    private var isConnected = false
    private var wifiManager: WifiManager? = null
    private var passableHostsPot: List<String>? = null
    private var intentFilter: IntentFilter? = null
    private var networkChangeReceiver: NetworkChangeReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_client)
        init()
    }

    fun init() {
        setContentView(R.layout.activity_client)
        isConnected = isConnectAp()
        intentFilter = IntentFilter()
        intentFilter!!.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        networkChangeReceiver = NetworkChangeReceiver()
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    private inner class WifiReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

        }
    }

    private fun isConnectAp(): Boolean {
        var connManager = getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        var mWifi = connManager.activeNetworkInfo
        //判断是否连接到指定wifi热点
        if (mWifi != null && mWifi.isConnected) {
            var SSID = mWifi.extraInfo
            Log.d("isConnectAP", SSID)
            if ("\"TEXTAP\"" == SSID) {
                Log.d("isConneced", "已连接到“TEXTAP”")
                Toast.makeText(this, "已正确连接网络", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        Toast.makeText(this, "请连接到WIFI：\"TEXTAP\"", Toast.LENGTH_SHORT).show()
        return false
    }

    inner class NetworkChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("Receiver", "网络状态改变")
            isConnected = isConnectAp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }
}
