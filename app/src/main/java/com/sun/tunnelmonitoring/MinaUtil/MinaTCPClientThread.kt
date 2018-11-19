package com.sun.tunnelmonitoring.MinaUtil

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import org.apache.mina.core.future.ConnectFuture
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.textline.LineDelimiter
import org.apache.mina.filter.codec.textline.TextLineCodecFactory
import org.apache.mina.transport.socket.nio.NioSocketConnector
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.UnknownHostException
import java.nio.charset.Charset


object MinaTCPClientThread: Runnable {
    val handler= Handler()
    private lateinit var session: IoSession
    private lateinit var connector: NioSocketConnector
    private lateinit var future: ConnectFuture
    lateinit var address: String

    fun connect(){
        Log.d("MinaTCPClientThread","连接服务器")
        Thread(this).start()
    }

    private fun setAPAddr(){
        //var wifiManager= context!!.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var wifiManager= MyApplication.getContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var dhcpInfo=wifiManager.dhcpInfo
        var hostAddress=dhcpInfo.serverAddress

        val addr=(hostAddress and 0xff).toString()+"."+((hostAddress shr 8) and 0xff).toString() +"."+((hostAddress shr 16) and 0xff).toString() +"."+((hostAddress shr 24) and 0xff).toString() +"."

        MinaTCPClientThread.address=addr
        println(hostAddress)
        println(addr)
    }

    override fun run() {
        setAPAddr()
        Log.d("MinaTCPClientThread","客户端连接开始...")
        connector= NioSocketConnector()
        connector.filterChain.addLast("codec",
            ProtocolCodecFilter(TextLineCodecFactory(Charset.forName("UTF-8"),
                LineDelimiter.WINDOWS.value, LineDelimiter.WINDOWS.value)))
        connector.connectTimeoutCheckInterval=10000
        connector.handler=MinaClientHandler()
        //var addr:InetAddress = getAPAddr()
        //var addr=InetSocketAddress("192.168.43.1",8888)
        var addr=InetSocketAddress(address,3344)
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
        future= connector.connect()
        future.awaitUninterruptibly()
        try {
            session = future.session
            handler.post {//给SessionManager中的session赋值
                SessionManager.session=session
                //SessionManager.writeMsg("start")
                handler.post {
                    Toast.makeText(MyApplication.getContext(),"连接服务器成功",Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e:Exception){
            Log.d("MinaTCPClientThread-run","客户端连接异常")
            handler.post {
                Toast.makeText(MyApplication.getContext(),"连接服务器异常",Toast.LENGTH_SHORT).show()
            }
        }
    }
}