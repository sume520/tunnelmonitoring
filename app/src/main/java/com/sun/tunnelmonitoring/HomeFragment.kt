package com.sun.tunnelmonitoring

import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.sun.tunnelmonitoring.MinaUtil.TCP.MinaTCPClientThread
import com.sun.tunnelmonitoring.MinaUtil.TCP.MinaTCPServerThread
import com.sun.tunnelmonitoring.MinaUtil.SessionManager
import com.sun.tunnelmonitoring.MinaUtil.UDP.MinaUDPClientThread
import com.sun.tunnelmonitoring.MinaUtil.UDP.MinaUDPServerThread
import com.sun.tunnelmonitoring.MinaUtil.wifiUtil
import kotlinx.android.synthetic.main.fragment_home.*
import com.threshold.logger.PrettyLogger

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.sun.tunnelmonitoring.File.FileInfo
import com.sun.tunnelmonitoring.File.FileUtil
import org.apache.mina.core.buffer.IoBuffer
import java.io.*


class HomeFragment : Fragment(), PrettyLogger {
    private lateinit var textview: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //注册Eventbus事件
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_home, container, false)
        textview=view.findViewById(R.id.tv_rectext)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        radio_netprotocal.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.tcpserver-> {
                    //MinaUDPServerThread.closeServer()
                    Log.d("HomeFragment","选中tcp服务器")
                    et_sendtext.isClickable=false
                    //AP_TCPService.startActionOrd(this!!.context!!)
                    MinaTCPServerThread.startTCPServer()
                    tv_ap_address.text=""
                }
                R.id.udpserver-> {
                    //MinaTCPServerThread.closeServer()
                    et_sendtext.isClickable=false
                    MinaUDPServerThread.startUDPServer()
                    tv_ap_address.text=""
                }
                R.id.tcpclient-> {
                    //setAPAddr()
                    et_sendtext.isClickable=true
                    MinaTCPClientThread.connect()
                    tv_ap_address.text=wifiUtil.getAPAddress()
                }
                R.id.udpclient-> {
                    et_sendtext.isClickable=true
                    MinaUDPClientThread.connect()
                    tv_ap_address.text=wifiUtil.getAPAddress()
                }
            }
        }

        send.setOnClickListener {
            var text=et_sendtext.text.toString()
            SessionManager.write(text)
            et_sendtext.text.clear()
        }

        bt_sendfile.setOnClickListener{
            //var filename = "tunneldata.txt"
            var filename="tunneldata.txt"
            var data = FileUtil.readFile(filename)
            println(data)
            if(data!=null) {
                var fileInfo = FileInfo(filename, data!!.size, data)
                SessionManager.write(fileInfo)
            }else
                Toast.makeText(context,"找不到文件",Toast.LENGTH_SHORT).show()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    //在UI线程中处理EventBus事件
    fun onUIUpdateEvent(messageEvent: MessageEvent){
        textview.append(messageEvent.message+"\n")
        view!!.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
