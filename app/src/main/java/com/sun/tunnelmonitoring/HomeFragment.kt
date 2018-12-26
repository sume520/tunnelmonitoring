package com.sun.tunnelmonitoring

import UDPServer
import UdpUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sun.tunnelmonitoring.MinaUtil.SessionManager
import com.sun.tunnelmonitoring.MinaUtil.TCP.MinaTCPClientThread
import com.sun.tunnelmonitoring.MinaUtil.TCP.MinaTCPServerThread
import com.sun.tunnelmonitoring.MinaUtil.wifiUtil
import com.threshold.logger.PrettyLogger
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


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
        var view = inflater.inflate(R.layout.fragment_home, container, false)
        textview = view.findViewById(R.id.tv_rectext)
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
            when (checkedId) {
                R.id.tcpserver -> {
                    //MinaUDPServerThread.closeServer()
                    Log.d("HomeFragment", "选中tcp服务器")
                    et_sendtext.isClickable = false
                    //AP_TCPService.startActionOrd(this!!.context!!)
                    MinaTCPServerThread.startTCPServer()
                    tv_ap_address.text = ""
                }
                R.id.udpserver -> {
                    //MinaTCPServerThread.closeServer()
                    et_sendtext.isClickable = false
                    UDPServer.start()
                    tv_ap_address.text = ""
                }
                R.id.tcpclient -> {
                    //setAPAddr()
                    et_sendtext.isClickable = true
                    MinaTCPClientThread.connect()
                    tv_ap_address.text = wifiUtil.getAPAddress()
                }
                R.id.udpclient -> {
                    et_sendtext.isClickable = true
                    UdpUtil.start()
                    tv_ap_address.text = wifiUtil.getAPAddress()
                }
            }
        }

        send.setOnClickListener {
            var text = et_sendtext.text.toString()
            SessionManager.write(text)
            et_sendtext.text.clear()
        }

        bt_sendfile.setOnClickListener {
            //var filename = "tunneldata.txt"
            var filename = "tunneldata.txt"
            UdpUtil.write(filename)
        }

        bt_senddata.setOnClickListener {
            UdpUtil.write("01 07 24 15 08 04 12 34 00 10 05 DC 00 5F 5A BA 00 00 D2 B8 01 F9 01 EA 2B 56 27 0F 0B 0B 00 C8 64 38 34 45 00 07 00 01 0F FF 0E A9")
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    //在UI线程中处理EventBus事件
    fun onUIUpdateEvent(messageEvent: MessageEvent) {
        textview.append(messageEvent.message + "\n")
        view!!.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
