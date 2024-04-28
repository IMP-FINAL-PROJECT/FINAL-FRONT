package com.imp.data.tracking.data.dao

/**
 * Screen On/Off Dao
 */
data class ScreenDao (

    // state (ON / OFF)
    var state: String? = null,

    // time stamp
    var timestamp: Long = 0L
)