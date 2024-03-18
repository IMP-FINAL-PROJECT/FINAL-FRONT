package com.imp.data.local.dao

/**
 * Main - Log Entity
 */
data class LogDao (

    var screenTime: LogValue = LogValue(),
    var screenAwake: LogValue = LogValue(),
    var step: LogValue = LogValue(),
    var light: LogValue = LogValue(),
    var location: LogPointValue = LogPointValue(),
) {

    data class LogValue (

        var max: Float = 0f,
        var valueList: ArrayList<Float> = ArrayList()
    )

    data class LogPointValue (

        var valueList: ArrayList<ArrayList<Double>> = ArrayList()
    )
}