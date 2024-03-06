package com.imp.domain.model

/**
 * Main - Log Model
 */
data class LogModel (

    // screen time
    var screenTime: LogValue = LogValue(),

    // screen awake count
    var screenAwake: LogValue = LogValue(),

    // step
    var step: LogValue = LogValue(),

    // light
    var light: LogValue = LogValue()
) {

    data class LogValue (

        var max: Float = 0f,
        var valueList: ArrayList<Float> = ArrayList()
    )
}