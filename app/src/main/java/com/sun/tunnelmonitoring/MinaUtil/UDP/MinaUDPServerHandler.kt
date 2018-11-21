package com.sun.tunnelmonitoring.MinaUtil.UDP

import android.util.Log
import com.sun.tunnelmonitoring.MessageEvent
import com.sun.tunnelmonitoring.MinaUtil.SessionManager
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.service.IoHandler
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.core.session.IoSession
import org.greenrobot.eventbus.EventBus
import java.net.DatagramPacket
import java.nio.charset.Charset

class MinaUDPServerHandler:IoHandlerAdapter() {
    override fun messageReceived(session: IoSession?, message: Any?) {
        super.messageReceived(session, message)
        var rec_text= SessionManager.IoBufferToString(message as IoBuffer)
        rec_text="接收到客户端消息："+rec_text
        Log.d("MinaUDPServerHandler",rec_text)
        EventBus.getDefault().post(MessageEvent(rec_text!!))
    }

    override fun sessionOpened(session: IoSession?) {
        super.sessionOpened(session)
        var addr=session!!.remoteAddress
        Log.d("MinaUDPServerHandler","连接到客户端："+addr)
    }

    override fun sessionClosed(session: IoSession?) {
        super.sessionClosed(session)
        Log.d("MinaUDPServerHandler","UDP服务器已关闭")
    }

    override fun messageSent(session: IoSession?, message: Any?) {
        super.messageSent(session, message)
        Log.d("MinaUDPServerHandler","向UDP客户端发送数据")
    }

    override fun inputClosed(session: IoSession?) {
        super.inputClosed(session)

    }

    override fun sessionCreated(session: IoSession?) {
        super.sessionCreated(session)
        val remoteAddress = session!!.getRemoteAddress()
        Log.d("MinaUDPClientHandler","连接到客户端："+remoteAddress)
    }

    override fun sessionIdle(session: IoSession?, status: IdleStatus?) {
        super.sessionIdle(session, status)
        Log.d("MinaUDPServerHandler","UDP服务器空闲中")
    }

    override fun exceptionCaught(session: IoSession?, cause: Throwable?) {
        super.exceptionCaught(session, cause)
        Log.d("MinaUDPServerHandler","连接出错："+cause.toString())
    }

}