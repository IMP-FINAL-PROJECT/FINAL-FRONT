package com.imp.data.util

import com.imp.data.BuildConfig

/**
 * Http Constants
 */
class HttpConstants {

    companion object {

        // Server define
        private const val SERVICE = 0
        private const val DEV = 1

        // Current
        //--------------------------------------------------
        private const val SERVER: Int = DEV

        @JvmStatic
        fun getHost(): String = when (BuildConfig.DEBUG) {

            true -> when(SERVER) {
                SERVICE -> SERVICE_SERVER_HOST
                else -> DEV_SERVER_HOST
            }
            false -> SERVICE_SERVER_HOST
        }

        // Server Release Url
        //--------------------------------------------------
        private const val SERVICE_SERVER_HOST = BuildConfig.SERVICE_SERVER_HOST
        private const val DEV_SERVER_HOST = BuildConfig.DEV_SERVER_HOST

        // Api url
        const val API_SENSOR_INSERT = "/api/sensor/insert"          // 센싱 데이터 저장
    }
}