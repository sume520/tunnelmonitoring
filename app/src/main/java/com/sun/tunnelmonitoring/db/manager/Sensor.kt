package com.sun.tunnelmonitoring.db.manager

import org.litepal.crud.LitePalSupport

data class Sensor(
    var number: Int,
    var sensor_type: String,
    var create_time: String,
    var is_delete: Int,
    var running_state: Int,
    var scan_rate: Int,
    var sample_quality: Int,
    var sensor_rate: Int,
    var hign_rate: Int,
    var low_rate: Int,
    var input_voltage: Float,
    var loop_resistance: Float,
    var excitation_resistance: Float,
    var temperature: Float,
    var sample_standard_deviation: Float,
    var sample_number: Int,
    var high_sign: Int,
    var low_sign: Int,
    var GPIO: Int,
    var ADC02: Int,
    var ADC03: Int,
    var ADC04: Int,
    var sensor_number_id: Int
) : LitePalSupport()