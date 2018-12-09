package com.sun.tunnelmonitoring.MinaUtil.UDP

import android.util.Log
import com.sun.tunnelmonitoring.MessageEvent
import com.sun.tunnelmonitoring.MinaUtil.SessionManager
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.core.session.IoSession
import org.greenrobot.eventbus.EventBus

class MinaUDPClientHandler : IoHandlerAdapter() {
    override fun messageReceived(session: IoSession?, message: Any?) {
        super.messageReceived(session, message)
        var rec_text = SessionManager.IoBufferToString(message as IoBuffer)
        rec_text = "接收到服务器消息：" + rec_text
        Log.d("MinaUDPClientHandler", rec_text)
        EventBus.getDefault().post(MessageEvent(rec_text!!))
    }

    override fun sessionOpened(session: IoSession?) {
        super.sessionOpened(session)
        Log.d("MinaUDPClientHandler", "UDP客户端已打开")
    }

    override fun sessionClosed(session: IoSession?) {
        super.sessionClosed(session)
        Log.d("MinaUDPClientHandler", "已关闭UDP客户端")
    }

    override fun messageSent(session: IoSession?, message: Any?) {
        super.messageSent(session, message)
        Log.d("MinaUDPClientHandler", "客服端向服务器发送消息")
    }

    override fun inputClosed(session: IoSession?) {
        super.inputClosed(session)
        Log.d("MinaUDPClientHandler", "输入关闭")
    }

    override fun sessionCreated(session: IoSession?) {
        super.sessionCreated(session)
        Log.d("MinaUDPClientHandler", "与服务器的连接创建成功")
    }

    override fun sessionIdle(session: IoSession?, status: IdleStatus?) {
        super.sessionIdle(session, status)
        Log.d("MinaUDPClientHandler", "空闲中")
    }

    override fun exceptionCaught(session: IoSession?, cause: Throwable?) {
        super.exceptionCaught(session, cause)
        Log.d("MinaUDPClientHandler", "连接出错：" + cause.toString())
    }

}
