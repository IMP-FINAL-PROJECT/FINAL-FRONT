package com.imp.domain.model

/**
 * Tracking Sensor Model
 */
data class SensorModel (

    var id: String? = null,

    var illuminance: ArrayList<Int> = ArrayList(),

    var pedometer: Int = 0,

    var screenFrequency: Int = 0,

    var screenDuration: Int = 0,

    var gps: ArrayList<Array<Double>> = ArrayList(),

    var timestamp: String? = null,

    var hour: Int = 0,

    var minTimestamp: Long = 0L
)