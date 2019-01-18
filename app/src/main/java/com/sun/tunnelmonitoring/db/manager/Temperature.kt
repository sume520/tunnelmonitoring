package com.sun.tunnelmonitoring.db.manager

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

data class Temperature(
    @Column(nullable = false)
    var temp:Float,
    @Column(nullable = false)
    var date:String,
    @Column(nullable = false)
    var time:String
):LitePalSupport(){
    override fun toString(): String {
        return "temp: $temp, date: $date, time: $time"
    }
}