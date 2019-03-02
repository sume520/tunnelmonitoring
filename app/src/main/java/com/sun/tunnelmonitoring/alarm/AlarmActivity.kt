package com.sun.tunnelmonitoring.alarm

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import com.sun.tunnelmonitoring.R
import kotlinx.android.synthetic.main.activity_alarm.*

class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        title="数据警报"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "alert"
            val channelName = "警报"
            val importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(channelId, channelName, importance)
        }

        bt_alert.setOnClickListener {
            newAlert()
        }
    }

    private fun newAlert(){
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
        val resultIntent= Intent(this,AlarmActivity::class.java)
        val pendingIntent=PendingIntent
            .getActivity(
                this,0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this, "alert")
            .setContentTitle("警报信息")
            .setContentText("有测点检测到异常数据")
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_alert)
            .setContentIntent(pendingIntent)
            .setLargeIcon(
                BitmapFactory
                    .decodeResource(
                        resources, R.drawable.ic_alert))
            .build()
        //警报
        notification.flags= Notification.FLAG_AUTO_CANCEL
        manager.notify(1, notification)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        importance: Int
    ) {//创建通知渠道
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }
}
