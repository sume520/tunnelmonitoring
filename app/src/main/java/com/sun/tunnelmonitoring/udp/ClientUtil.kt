package com.sun.tunnelmonitoring.udp

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

object ClientUtil {
    private val IP = "47.107.158.26"
    private val PORT = 7788
    private val addr = InetSocketAddress(IP, PORT)

    fun send(msg: String) {

        Thread {
            var buf = ByteArray(1024 * 8 * 64)
            var packet_send = DatagramPacket(msg.toByteArray(), msg.length)
            var packet_recv = DatagramPacket(buf, 1024 * 8 * 64)
            val s = DatagramSocket(addr)
            try {
                s.send(packet_send)
            } catch (e: Exception) {
                Log.e("ClientUtil", "发送数据出错")
            }
            try {
                s.receive(packet_recv)
                Log.d("ClientUtil", packet_recv.data.toString())
            } catch (e: Exception) {
                Log.e("ClientUtil", "接受数据出错")
            }
        }.start()

    }
}


