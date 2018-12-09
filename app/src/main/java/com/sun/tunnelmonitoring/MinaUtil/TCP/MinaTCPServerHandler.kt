package com.sun.tunnelmonitoring.MinaUtil.TCP

import android.util.Log
import com.sun.tunnelmonitoring.MessageEvent
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.core.session.IoSession
import org.greenrobot.eventbus.EventBus

class MinaTCPServerHandler : IoHandlerAdapter() {
    override fun messageReceived(session: IoSession?, message: Any?) {
        super.messageReceived(session, message)
        var rec_msg = message.toString()
        Log.d("MinaTCPServerHandler", rec_msg)
        EventBus.getDefault().post(MessageEvent("接收到客户端消息：" + rec_msg))
        session!!.write("Hello World!")
    }

    override fun sessionOpened(session: IoSession?) {
        super.sessionOpened(session)
    }

    override fun sessionClosed(session: IoSession?) {
        super.sessionClosed(session)
        Log.d("MinaTCPServerHandler", "关闭连接")
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
        session!!.closeNow()
        Log.d("MinaTCPServerHandler", "发生异常")
    }
}