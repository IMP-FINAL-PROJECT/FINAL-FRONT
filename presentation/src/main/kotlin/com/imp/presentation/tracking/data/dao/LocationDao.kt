package com.imp.presentation.tracking.data.dao

/**
 * Location Dao
 */
data class LocationDao (

    // 위도
    var latitude: Float = 0f,

    // 경도
    var longitude: Float = 0f,

    // time stamp
    var timestamp: Long = 0L
)