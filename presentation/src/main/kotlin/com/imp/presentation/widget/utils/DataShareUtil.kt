package com.imp.presentation.widget.utils

/**
 * Data Share Util
 */
class DataShareUtil {

    companion object {

        @Volatile private var instance: DataShareUtil? = null
        @JvmStatic fun getInstance(): DataShareUtil =
            instance ?: synchronized(this) {
                instance ?: DataShareUtil().also {
                    instance = it
                }
            }
    }
}