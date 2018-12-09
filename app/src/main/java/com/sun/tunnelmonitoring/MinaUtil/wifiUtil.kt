package com.sun.tunnelmonitoring.MinaUtil

import android.content.Context
import android.net.wifi.WifiManager
import com.sun.tunnelmonitoring.MyApplication

object wifiUtil {

    fun getAPAddress(): String {
        var wifiManager =
            MyApplication.getContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var dhcpInfo = wifiManager.dhcpInfo
        var hostAddress = dhcpInfo.serverAddress
        val addr =
            (hostAddress and 0xff).toString() + "." + ((hostAddress shr 8) and 0xff).toString() + "." + ((hostAddress shr 16) and 0xff).toString() + "." + ((hostAddress shr 24) and 0xff).toString() + "."

        return addr
    }

}