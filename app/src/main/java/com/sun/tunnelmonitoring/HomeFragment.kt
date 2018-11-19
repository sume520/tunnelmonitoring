package com.sun.tunnelmonitoring


import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.sun.tunnelmonitoring.MinaUtil.MinaTCPClientThread
import com.sun.tunnelmonitoring.MinaUtil.MinaTCPServerThread
import com.sun.tunnelmonitoring.MinaUtil.SessionManager
import com.threshold.logger.PrettyLogger
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.net.InetAddress

class HomeFragment : Fragment(), PrettyLogger {
    private lateinit var textview:TextView

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
                    Log.d("HomeFragment","选中tcp服务器")
                    et_sendtext.isClickable=false
                    //AP_TCPService.startActionOrd(this!!.context!!)
                    MinaTCPServerThread.startTCPServer()
                }
                R.id.udpserver-> {
                    et_sendtext.isClickable=false
                    //MinaTCPClientThread.connect()
                }
                R.id.tcpclient-> {
                    //setAPAddr()
                    et_sendtext.isClickable=true
                    MinaTCPClientThread.connect()
                }
                R.id.udpclient-> {
                    et_sendtext.isClickable=true
                }
            }
        }

        send.setOnClickListener {
            SessionManager.writeMsg(et_sendtext.text.toString())
            et_sendtext.text.clear()
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
