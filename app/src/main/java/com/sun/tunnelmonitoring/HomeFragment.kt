package com.sun.tunnelmonitoring

import UDPServer
import UdpUtil
import android.annotation.SuppressLint
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
import com.sun.tunnelmonitoring.tree.TreeFragment
import com.threshold.logger.PrettyLogger
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_monitor.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HomeFragment : Fragment(), PrettyLogger {
    private lateinit var textview: TextView
    private var serverStates=false
    private var clientStates=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //注册Eventbus事件
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        return view
    }

   companion object {
       @SuppressLint("StaticFieldLeak")
       private var instance:HomeFragment?=null
       get() {
           if(field==null){
               field= HomeFragment()
           }
           return field
       }
       @Synchronized
       fun get():HomeFragment{
           return instance!!
       }
   }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*send.setOnClickListener {
            var text = et_sendtext.text.toString()
            SessionManager.write(text)
            et_sendtext.text.clear()
        }
*/
       /* sw_udpserver.setOnCheckedChangeListener { buttonView, isChecked ->
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
*/
        sw_tcpserver.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                TcpServer.start()
                serverStates=true
            }else{
                TcpServer.close()
                serverStates=false
            }
            tv_ap_address.text=""
        }

        sw_tcpclient.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                TcpClient.start()
                clientStates=true
                tv_ap_address.text = wifiUtil.getAPAddress()
            }else{
                TcpClient.close()
                clientStates=false
                tv_ap_address.text=""
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
        tv_rectext.append(messageEvent.message + "\n")
            view!!.invalidate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("serverstates",serverStates)
        outState.putBoolean("clientstates",clientStates)
        Log.i("onLoadInstanceState","serverstates: $serverStates, clientstates: $clientStates")
    }

    private fun onLoadInstanceState(inState: Bundle){
        serverStates= inState.getBoolean("serverstates",false)
        clientStates= inState.getBoolean("clientstates",false)
        Log.i("onLoadInstanceState","serverstates: $serverStates, clientstates: $clientStates")
        sw_tcpserver.isChecked=serverStates
        sw_tcpclient.isChecked=clientStates
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        Log.i("HomeFragment","destroy")
        super.onDestroy()
    }

}
