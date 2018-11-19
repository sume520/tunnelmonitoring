package com.sun.tunnelmonitoring.MinaUtil

import android.util.Log
import com.sun.tunnelmonitoring.MessageEvent
import org.apache.mina.core.service.IoHandler
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.core.session.IoSession
import org.greenrobot.eventbus.EventBus

class MinaClientHandler : IoHandlerAdapter() {
    override fun messageReceived(session: IoSession?, message: Any?) {
        super.messageReceived(session, message)
        var rec_msg=message.toString()
        Log.d("MinaServerHandler",rec_msg)
        EventBus.getDefault().post(MessageEvent(rec_msg))
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
