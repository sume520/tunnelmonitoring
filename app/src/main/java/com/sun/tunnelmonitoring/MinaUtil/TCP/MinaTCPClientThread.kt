package com.sun.tunnelmonitoring.MinaUtil.TCP

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MinaUtil.SessionManager
import com.sun.tunnelmonitoring.MinaUtil.wifiUtil
import com.sun.tunnelmonitoring.MyApplication
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.future.ConnectFuture
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.textline.LineDelimiter
import org.apache.mina.filter.codec.textline.TextLineCodecFactory
import org.apache.mina.transport.socket.nio.NioSocketConnector
import java.net.InetSocketAddress
import java.nio.charset.Charset


object MinaTCPClientThread: Runnable {
    val handler= Handler()
    private lateinit var session: IoSession
    private lateinit var connector: NioSocketConnector
    private lateinit var future: ConnectFuture
    private var address=wifiUtil.getAPAddress()

    fun connect(){
        Log.d("MinaTCPClientThread","连接服务器")
        Thread(this).start()
    }

    override fun run() {
        Log.d("MinaTCPClientThread","客户端连接开始...")
        connector = NioSocketConnector()
        //添加过滤器
        connector.filterChain.addLast("codec",
            ProtocolCodecFilter(TextLineCodecFactory(Charset.forName("UTF-8"),
                LineDelimiter.WINDOWS.value, LineDelimiter.WINDOWS.value)))
        //设置超时
        connector.connectTimeoutCheckInterval=10000

        connector.handler= MinaTCPClientHandler()

        //用本地IP调试
        address="localhost"
        var addr=InetSocketAddress(address,3344)
        //var addr=InetSocketAddress("localhost",3344)
        connector.setDefaultRemoteAddress(addr)
        // 监听客户端是否断线
        connector.addListener(object : IoListener() {//监听连接状态，断线 重连

            @Throws(Exception::class)
            override fun sessionDestroyed(arg0: IoSession?) {
                super.sessionDestroyed(arg0)
                Log.d("ConnectUtil","连接断开，尝试重连...")
                try {
                    val failCount = 0
                    while (true) {
                        Thread.sleep(5000)
                        println((connector
                            .defaultRemoteAddress as InetSocketAddress)
                            .address
                            .hostAddress)
                        future = connector.connect()
                        future.awaitUninterruptibly()
                        session = future.session
                        if (session != null && session.isConnected()) {
                            println("断线重连["
                                    + (session.getRemoteAddress() as InetSocketAddress).address.hostAddress
                                    + ":" + (session.getRemoteAddress() as InetSocketAddress).port + "]成功")
                            session.write("start")
                            break
                        } else {
                            println("断线重连失败--->" + failCount + "次")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })

        try {
            future = connector.connect()
            future.awaitUninterruptibly()
            session = future.session

            session.write("This message is from TCP Client")

            handler.post {//给SessionManager中的session赋值
                SessionManager.session = session
                Toast.makeText(MyApplication.getContext(),"连接服务器成功",Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){
            Log.d("MinaTCPClientThread-run","客户端连接异常")
            handler.post {
                Toast.makeText(MyApplication.getContext(),"连接服务器异常",Toast.LENGTH_SHORT).show()
            }
        }
    }
}