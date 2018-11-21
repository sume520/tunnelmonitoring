package com.sun.tunnelmonitoring.MinaUtil.TCP

import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MessageEvent
import com.sun.tunnelmonitoring.MinaUtil.SessionManager
import com.sun.tunnelmonitoring.MyApplication
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.core.session.IoSession
import org.greenrobot.eventbus.EventBus

class MinaTCPClientHandler : IoHandlerAdapter() {
    override fun messageReceived(session: IoSession?, message: Any?) {
        super.messageReceived(session, message)
        var rec_msg= message.toString()
        Log.d("MinaTCPServerHandler",rec_msg)
        EventBus.getDefault().post(MessageEvent("接收到服务器消息："+rec_msg))
        Toast.makeText(MyApplication.getContext(),rec_msg,Toast.LENGTH_SHORT).show()
    }

    override fun sessionOpened(session: IoSession?) {
        super.sessionOpened(session)
    }

    override fun sessionClosed(session: IoSession?) {
        super.sessionClosed(session)
    }

    override fun messageSent(session: IoSession?, message: Any?) {
        super.messageSent(session, message)

    }

    override fun inputClosed(session: IoSession?) {
        super.inputClosed(session)
    }

    override fun sessionCreated(session: IoSession?) {
        super.sessionCreated(session)
    }

    override fun sessionIdle(session: IoSession?, status: IdleStatus?) {
        super.sessionIdle(session, status)
    }

    override fun exceptionCaught(session: IoSession?, cause: Throwable?) {
        super.exceptionCaught(session, cause)
    }
}
