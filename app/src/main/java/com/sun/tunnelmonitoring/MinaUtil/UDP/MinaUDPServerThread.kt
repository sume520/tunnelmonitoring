package com.sun.tunnelmonitoring.MinaUtil.UDP

import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor
import java.net.InetSocketAddress

object MinaUDPServerThread : Runnable {
    private var handler = Handler()
    private lateinit var acceptor: NioDatagramAcceptor

    fun startUDPServer() {
        Thread(this).start()
    }

    override fun run() {
        try {
            acceptor = NioDatagramAcceptor()
            acceptor.handler = MinaUDPServerHandler()
            // 设置读取数据的换从区大小
            acceptor.sessionConfig.readBufferSize = 2048
            // 读写通道10秒内无操作进入空闲状态
            acceptor.sessionConfig.setIdleTime(IdleStatus.BOTH_IDLE, 10)

            //设置过滤器
            /*val chain = acceptor.filterChain
            chain.addLast("logger", LoggingFilter())
            acceptor.filterChain.addLast("codec",
                ProtocolCodecFilter(
                    TextLineCodecFactory(
                        Charset.forName("UTF-8"),
                        LineDelimiter.WINDOWS.value, LineDelimiter.WINDOWS.value)
                )
            )*/
            acceptor.filterChain.addLast("codec", ProtocolCodecFilter(ObjectSerializationCodecFactory()))

            val dcfg = acceptor.sessionConfig
            dcfg.setReuseAddress(true)

            acceptor.bind(InetSocketAddress(3344))
            handler.post {
                Toast.makeText(MyApplication.getContext(), "启动UDP服务成功", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.d("MinaUDPServerThread", "启动UDP服务失败")
            handler.post {
                Toast.makeText(MyApplication.getContext(), "启动UDP服务失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

}