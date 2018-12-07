package com.sun.tunnelmonitoring.MinaUtil.UDP

import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.File.FileInfo
import com.sun.tunnelmonitoring.File.FileUtil
import com.sun.tunnelmonitoring.MessageEvent
import com.sun.tunnelmonitoring.MinaUtil.SessionManager
import com.sun.tunnelmonitoring.MyApplication
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.service.IoHandler
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.core.session.IoSession
import org.greenrobot.eventbus.EventBus
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.io.ObjectStreamException
import java.net.DatagramPacket
import java.nio.charset.Charset

class MinaUDPServerHandler:IoHandlerAdapter() {
    override fun messageReceived(session: IoSession?, message: Any?) {
        super.messageReceived(session, message)

        /*var rec_text=message.toString()
        rec_text="接收到客户端消息："+rec_text
        Log.d("MinaUDPServerHandler",rec_text)*/
        //EventBus.getDefault().post(MessageEvent(rec_text!!))
        Log.d("MinaUDPServerHandler","准备接受文件")
        try{
            var fileinfo = message as FileInfo
            println("文件名：${fileinfo.filename}")
            println("文件大小：${fileinfo.filesize}")
            println("文件数据：${fileinfo.filedata}")
            var filetype=fileinfo.filename.split('.')[1]
            var str="""接收到文件
                |文件名：${fileinfo.filename}
                |文件大小：${fileinfo.filesize}
                |文件内容：${String(fileinfo.filedata)}
            """.trimMargin()
            EventBus.getDefault().post(MessageEvent(str))

            //FileUtil.writeFile("test1.txt",fileinfo.filedata)
            FileUtil.writeFile("test1.${filetype}",fileinfo.filedata)
        }catch (e:ObjectStreamException){
            Log.d("MinaUDPServerHandler","不可转成FileInfo对象")
            //var rec=SessionManager.IoBufferToString()
            //EventBus.getDefault().post(MessageEvent(rec!!))
        }
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
        Log.d("MinaUDPServerHandler","连接到客户端："+remoteAddress)
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