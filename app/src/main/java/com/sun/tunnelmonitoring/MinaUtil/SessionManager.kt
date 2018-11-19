package com.sun.tunnelmonitoring.MinaUtil

import android.util.Log
import org.apache.mina.core.session.IoSession
import java.io.IOException
import java.util.*

object SessionManager {
    var session: IoSession? = null//session将在MinaThread中赋值

    fun writeMsg(msg:String){
        if(session!=null && session!!.isConnected){
            try{
                session!!.write(msg)
            }catch (e:IOException){
                Log.d("writeMsg","发送数据异常")
            }
        }
    }

}