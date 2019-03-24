package com.sun.tunnelmonitoring

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.Gson
import com.sun.tunnelmonitoring.Utils.SENSORS
import com.sun.tunnelmonitoring.db.manager.SensorInfo
import com.sun.tunnelmonitoring.db.manager.SensorInfoList
import com.sun.tunnelmonitoring.events.MessageEvent
import com.sun.tunnelmonitoring.projectTree.ProjectTreeActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_home.*
import lecho.lib.hellocharts.gesture.ContainerScrollType
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import org.litepal.extension.deleteAll
import org.litepal.extension.findAll
import java.net.URL
import java.security.SecureRandom

class HomeFragment : Fragment() {

    private val handler=Handler()

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: HomeFragment? = null
            get() {
                if (field == null) {
                    field = HomeFragment()
                }
                return field
            }

        @Synchronized
        fun get(): HomeFragment {//单例模式
            return instance!!
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //注册Eventbus事件
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bt_baseinform.setOnClickListener {
            val intent = Intent(context, ProjectTreeActivity::class.java)
            val option = ActivityOptions.makeSceneTransitionAnimation(activity)
            startActivity(intent, option.toBundle())
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    //在UI线程中处理EventBus事件
    fun onUIUpdateEvent(messageEvent: MessageEvent) {
        Toast.makeText(activity, messageEvent.message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

}
