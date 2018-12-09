package com.sun.tunnelmonitoring.MinaUtil.TCP

import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import org.apache.mina.core.service.IoAcceptor
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.textline.LineDelimiter
import org.apache.mina.filter.codec.textline.TextLineCodecFactory
import org.apache.mina.transport.socket.nio.NioSocketAcceptor
import java.net.InetSocketAddress
import java.nio.charset.Charset


object MinaTCPServerThread : Runnable {
    private var handler = Handler()
    private val PORT = 3344
    private lateinit var acceptor: IoAcceptor

    fun startTCPServer() {
        Thread(this).start()
    }

    override fun run() {
        try {
            // 创建一个非阻塞的server端的Socket
            acceptor = NioSocketAcceptor()
            // 设置过滤器（使用mina提供的文本换行符编解码器）
            acceptor.filterChain.addLast(
                "codec",
                ProtocolCodecFilter(
                    TextLineCodecFactory(
                        Charset.forName("UTF-8"),
                        LineDelimiter.WINDOWS.value, LineDelimiter.WINDOWS.value
                    )
                )
            )
            // 自定义的编解码器
            // acceptor.getFilterChain().addLast("codec", new
            // ProtocolCodecFilter(new CharsetCodecFactory()));
            // 设置读取数据的换从区大小
            acceptor.sessionConfig.readBufferSize = 2048
            // 读写通道10秒内无操作进入空闲状态
            acceptor.sessionConfig.setIdleTime(IdleStatus.BOTH_IDLE, 10)
            // 为接收器设置管理服务
            acceptor.handler = MinaTCPServerHandler()
            // 绑定端口
            acceptor.bind(InetSocketAddress(PORT))
            Log.d("MinaTcpServerThread", "服务器启动成功... 端口号未：$PORT")
            handler.post {
                Toast.makeText(MyApplication.getContext(), "服务器启动成功", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.d("MinaTCPServerThread", "服务器启动异常...")
            e.printStackTrace()
            handler.post { Toast.makeText(MyApplication.getContext(), "服务器启动失败", Toast.LENGTH_SHORT).show() }
        }

    }
}