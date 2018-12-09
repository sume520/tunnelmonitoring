package com.sun.tunnelmonitoring.MinaUtil.UDP

import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MinaUtil.SessionManager
import com.sun.tunnelmonitoring.MinaUtil.wifiUtil
import com.sun.tunnelmonitoring.MyApplication
import org.apache.mina.core.future.ConnectFuture
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory
import org.apache.mina.transport.socket.nio.NioDatagramConnector
import java.net.InetSocketAddress


object MinaUDPClientThread : Runnable {
    var handler = Handler()
    private lateinit var session: IoSession
    //private lateinit var connector: NioDatagramConnector
    private lateinit var future: ConnectFuture
    private var address = wifiUtil.getAPAddress()

    fun connect() {
        Log.d("MinaUDPClientThread", "连接UDP服务器")
        Thread(this).start()
    }

    override fun run() {
        var connector = NioDatagramConnector()

        //过滤器
        /*connector.filterChain.addLast("codec",
            ProtocolCodecFilter(
                TextLineCodecFactory(
                    Charset.forName("UTF-8"),
                LineDelimiter.WINDOWS.value, LineDelimiter.WINDOWS.value)
            )
        )*/
        connector.filterChain.addLast("codec", ProtocolCodecFilter(ObjectSerializationCodecFactory()))

        connector.setHandler(MinaUDPClientHandler())
        //用本地IP调试
        address = "localhost"
        var addr = InetSocketAddress(address, 3344)
        connector.connectTimeoutCheckInterval = 10000
        connector.setDefaultRemoteAddress(addr)
        future = connector.connect()
        future.addListener {
            Log.d("MinaUDPClientThread", "1")
            var connFuture = future
            if (connFuture.isConnected) {
                session = future.session
                session.write("This message if from udp client")
                handler.post {
                    SessionManager.session = session
                    Log.d("MinaUDPClientThread", "等待UDP报文发送")
                    Toast.makeText(MyApplication.getContext(), "等待UDP报文发送", Toast.LENGTH_SHORT).show()
                }
            } else {
                handler.post {
                    Log.e("MinaUDPClientThread", "连接UDP服务器失败")
                    Toast.makeText(MyApplication.getContext(), "连接UDP服务器失败", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}