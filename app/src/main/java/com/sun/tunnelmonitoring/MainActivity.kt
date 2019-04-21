package com.sun.tunnelmonitoring

import android.Manifest
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.sun.tunnelmonitoring.alarm.AlarmActivity
import com.sun.tunnelmonitoring.events.AlarmEvent
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.EasyPermissions

private const val PERMISSION_REQUEST_READ_WRITE_EXTERNAL_STORAGE = 1

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    title = "主页"
                    supportFragmentManager.inTransaction {
                        replace(
                            R.id.activity_fragment,
                            HomeFragment.get()
                        )
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_monitor -> {
                    title = "监控中心"
                    supportFragmentManager.inTransaction {
                        replace(
                            R.id.activity_fragment,
                            MonitorFragment.get()
                        )
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_sysconfig -> {
                    title = "系统配置"
                    supportFragmentManager.inTransaction {
                        replace(
                            R.id.activity_fragment,
                            SysConfigFragment()
                        )
                    }
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AlarmTCPServer.start()

        supportFragmentManager.inTransaction { add(R.id.activity_fragment, HomeFragment.get()) }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        title = "主页"

        EventBus.getDefault().register(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//创建通知渠道
            val channelId = "alert"
            val channelName = "警报"
            val importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(channelId, channelName, importance)
        }

        //申请权限
        EasyPermissions.requestPermissions(
            this@MainActivity,
            "没有相应权限本软件将无法正常工作",
            PERMISSION_REQUEST_READ_WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    //权限申请回调
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_READ_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.size > 0) {
                println(grantResults.size)
                for (res in grantResults) {
                    if (res == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this, "成功获取到权限", Toast.LENGTH_SHORT).show()
                    else {
                        Toast.makeText(this, "获取权限被拒绝，重启软件后可重新获取", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun newAlert(contentTitle:String,contentText:String) {//发出警报通知
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
        val resultIntent= Intent(this, AlarmActivity::class.java)
        val pendingIntent= PendingIntent
            .getActivity(
                this,0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this, "alert")
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_alert)
            .setContentIntent(pendingIntent)
            .setLargeIcon(
                BitmapFactory
                    .decodeResource(
                        resources, R.drawable.ic_alert))
            .build()
        //警报通知
        notification.flags= Notification.FLAG_AUTO_CANCEL
        manager.notify(1, notification)
    }

    ////创建通知渠道
    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        importance: Int
    ) {
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun dealAlarmEvent(event: AlarmEvent){
        newAlert(event.contentTitle,event.contentText)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}
