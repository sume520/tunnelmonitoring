package com.sun.tunnelmonitoring.db.manager

import org.litepal.crud.LitePalSupport

enum class Sensors(val sensorName:String) {
    shenyaji("渗压计"),
    gangjinji("钢筋计"),
    maoganji("锚杆计"),
    yingbianji("应变计"),
    cefengji("测缝计"),
    weiyiji("位移计"),
    gangsuoji("钢索计"),
    liefengji("裂缝计"),
    tuyaliji("土压力计"),
    maosuoji("锚索计"),
    cexieyi("测斜仪"),
    wenduji("温度计")
}