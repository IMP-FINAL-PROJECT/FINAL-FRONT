package com.imp.data.tracking.util

import android.content.Context
import android.os.PowerManager

class MethodStorageUtil {

    companion object {

        /**
         * Check Current Screen On/Off
         *
         * @return
         */
        fun checkDeviceStatus(context: Context): Boolean {

            return try {

                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                powerManager.isInteractive

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}