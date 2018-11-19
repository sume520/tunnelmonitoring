package com.sun.tunnelmonitoring.MinaUtil

import org.apache.mina.core.service.IoService
import org.apache.mina.core.service.IoServiceListener
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.core.session.IoSession

open class IoListener:IoServiceListener {
    override fun sessionDestroyed(p0: IoSession?) {
    }

    override fun serviceActivated(p0: IoService?) {
    }

    override fun serviceDeactivated(p0: IoService?) {
    }

    override fun sessionClosed(p0: IoSession?) {
    }

    override fun sessionCreated(p0: IoSession?) {
    }

    override fun serviceIdle(p0: IoService?, p1: IdleStatus?) {
    }
}
