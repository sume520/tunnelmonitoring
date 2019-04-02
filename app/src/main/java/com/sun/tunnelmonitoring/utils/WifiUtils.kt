package com.sun.tunnelmonitoring.utils

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import com.sun.tunnelmonitoring.MyApplication


fun getAPIP(): String {
    val wifiManager = MyApplication.getContext()
            .applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
    val info = wifiManager!!.dhcpInfo
    Log.i("getAPIP", intToIp(info.serverAddress))
    return intToIp(info.serverAddress)
}

private fun intToIp(i: Int): String {
    return ((i and 0xFF).toString() + "." + (i shr 8 and 0xFF) + "." + (i shr 16 and 0xFF) + "."
            + (i shr 24 and 0xFF))
}