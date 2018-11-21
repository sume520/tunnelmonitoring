package com.sun.tunnelmonitoring.MinaUtil

import android.util.Log
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.IoSession
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

object SessionManager {
    var session: IoSession? = null//session将在MinaThread中赋值

    fun write(msg:Any){
        if(session!=null && session!!.isConnected){
            try{
                session!!.write(msg)
                Log.d("write","发送数据成功")
            }catch (e:IOException){
                Log.d("write","发送数据异常")
            }
        }
    }

    fun writeStr(msg:String){
        if(session!=null && session!!.isConnected){
            try{
                session!!.write(msg)
                Log.d("write","发送数据成功")
            }catch (e:IOException){
                Log.d("write","发送数据异常")
            }
        }
    }

    fun writeToBuffer(msg:String){
        var buffer= IoBuffer.allocate(8)
        buffer.setAutoExpand(true)
        buffer.putString(msg,Charset.forName("UTF-8").newEncoder())
        buffer.flip()
        if(session!=null && session!!.isConnected){
            try{
                session!!.write(buffer)
                Log.d("write","发送数据成功")
            }catch (e:IOException){
                Log.d("write","发送数据异常")
            }
        }
    }

    fun IoBufferToString(buffer:IoBuffer): String? {
        var str= buffer.getString(Charset.forName("UTF-8").newDecoder())
        return str
    }

    fun StringToIoBuffer(str:String): IoBuffer? {
        var buffer= IoBuffer.allocate(8)
        buffer.setAutoExpand(true)
        buffer.putString(str,Charset.forName("UTF-8").newEncoder())
        buffer.flip()
        return buffer
    }

}