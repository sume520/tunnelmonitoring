package com.sun.tunnelmonitoring.Utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
    @SuppressLint("SimpleDateFormat")
    fun strToDate(time:String): Date {
        return SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(time)
    }

    @SuppressLint("SimpleDateFormat")
    fun DateToStr(date:Date): String{
        return SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(date: Date):String{
        return SimpleDateFormat("HH:mm:ss").format(date)
    }
}