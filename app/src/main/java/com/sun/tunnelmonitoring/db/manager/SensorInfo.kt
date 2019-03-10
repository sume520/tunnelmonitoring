package com.sun.tunnelmonitoring.db.manager

import org.litepal.crud.LitePalSupport

data class SensorInfo(
    var create_time:String,
    var sensor_number:Int,
    var running_state:Int,
    var scan_rate:Int,
    var sample_quality:Int,
    var sensor_rate:Int,
    var hign_rate:Int,
    var low_rate:Int,
    var input_voltage:Int,
    var loop_resistance:Int,
    var excitation_resistance:Int,
    var temperature:Float,
    var sample_standard_deviation:Int,
    var sample_number:Int,
    var hign_sign:Int,
    var low_sign:Int,
    var GPIO:Int,
    var ADC02:Int,
    var ADC03:Int,
    var ADC04:Int
):LitePalSupport()
