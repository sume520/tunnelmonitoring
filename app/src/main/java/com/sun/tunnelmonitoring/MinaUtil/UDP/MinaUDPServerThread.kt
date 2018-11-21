package com.sun.tunnelmonitoring.MinaUtil.UDP

import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor
import org.apache.mina.filter.logging.LoggingFilter
import java.net.InetSocketAddress

object MinaUDPServerThread:Runnable {
    private var handler= Handler()

    fun startUDPServer(){
        Thread(this).start()
    }

    override fun run() {
        val acceptor = NioDatagramAcceptor()
        acceptor.handler = MinaUDPServerHandler()
        // 设置读取数据的换从区大小
        //acceptor.sessionConfig.readBufferSize = 2048
        // 读写通道10秒内无操作进入空闲状态
        //acceptor.sessionConfig.setIdleTime(IdleStatus.BOTH_IDLE, 10)

        //设置过滤器
        val chain = acceptor.filterChain
        chain.addLast("logger", LoggingFilter())

        val dcfg = acceptor.sessionConfig
        dcfg.setReuseAddress(true)
        try {
            acceptor.bind(InetSocketAddress(3344))
            handler.post {
                Toast.makeText(MyApplication.getContext(),"启动UDP服务成功",Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){
            Log.d("MinaUDPServerThread","启动UDP服务失败")
            handler.post {
                Toast.makeText(MyApplication.getContext(),"启动UDP服务失败",Toast.LENGTH_SHORT).show()
            }
        }
    }

}