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
        /*radio_netprotocal.setOnCheckedChangeListener { group, checkedId ->
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
        }*/
/*
        send.setOnClickListener {
            var text = et_sendtext.text.toString()
            SessionManager.write(text)
            et_sendtext.text.clear()
        }*/

        sw_udpserver.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){//启动服务器
                UDPServer.start()
            }else {//关闭服务器
                UDPServer.close()
            }
            tv_ap_address.text = ""
        }

        sw_udpclient.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){//启动客户端
                UdpClient.start()
                tv_ap_address.text = wifiUtil.getAPAddress()
            }else{//关闭客户端
                UdpClient.close()
                tv_ap_address.text=""
            }
        }

        sw_tcpserver.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                TcpServer.start()
            }else{
                TcpServer.close()
            }
            tv_ap_address.text=""
        }

        sw_tcpclient.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                TcpClient.start()
            }else{
                TcpClient.close()
            }
        }

        bt_sendfile.setOnClickListener {
            var filename = "tunneldata.txt"
            TcpClient.sendFile(filename)
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
