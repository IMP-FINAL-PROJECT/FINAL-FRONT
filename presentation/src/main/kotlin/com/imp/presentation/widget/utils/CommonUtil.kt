package com.imp.presentation.widget.utils

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat

/**
 * Common Util
 */
class CommonUtil {

    companion object {

        const val TAG = ">>>"

        /**
         * 디버깅
         *
         * @param _class
         */
        fun log(_class: Class<*>) {

            val name = _class.name
            var i = 0
            val size = name.split("\\.").toTypedArray().size
            var str = ""

            while (i < size) {

                str += name.split("\\.").toTypedArray()[i]

                if (i < size - 1) str += "/"
                i++
            }

            log("--------------------------------> $str")
        }

        fun log(vararg values: Any) {

            // if (!BuildConfig.DEBUG) return

            for (obj in values) {

                if (obj is String) {

                    try {

                        var msg: String = obj
                        while (msg.isNotEmpty()) {

                            msg = if (msg.length > 4000) {
                                Log.d(TAG, msg.substring(0, 4000) + "")
                                msg.substring(4000)
                            } else {
                                Log.d(TAG, msg + "")
                                ""
                            }
                        }
                    } catch (e: Exception) {
                        // e.printStackTrace()
                    }

                } else if (obj is Int) {

                    try {
                        Log.d(TAG, obj.toString() + "")
                    } catch (e: Exception) {
                        // e.printStackTrace()
                    }

                } else if (obj is Float) {

                    try {
                        Log.d(TAG, obj.toString() + "")
                    } catch (e: Exception) {
                        // e.printStackTrace()
                    }

                } else if (obj is Double) {

                    try {
                        Log.d(TAG, obj.toString() + "")
                    } catch (e: Exception) {
                        // e.printStackTrace()
                    }

                } else if (obj is Boolean) {

                    try {
                        Log.d(TAG, obj.toString() + "")
                    } catch (e: Exception) {
                        // e.printStackTrace()
                    }

                } else if (obj is Class<*>) {

                    log(obj)
                }
            }
        }

        /**
         * 자동 줄바꿈 방지
         */
        fun makeBreakableText(s: String?): String {

            val sb = StringBuilder()

            if (s == null) return ""

            for (element in s) {

                sb.append(element)
                sb.append("\u200B")
            }

            if (sb.isNotEmpty()) {

                sb.deleteCharAt(sb.length - 1)
            }

            return sb.toString()
        }

        /**
         * DP/PX 변환
         *
         * @param context
         * @param px
         * @return
         */
        fun dpFromPx(context: Context, px: Float): Float {
            val scale = context.resources.displayMetrics.density
            return px / scale
        }

        /**
         * DP/PX 변환
         *
         * @param context
         * @param dp
         * @return
         */
        @JvmStatic
        fun pxFromDp(context: Context, dp: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dp * scale
        }

        fun getDecimalFormat(num: Long): String? {
            val df = DecimalFormat("###,###")
            return df.format(num)
        }

        fun getDecimalFormat(num: Int): String? {
            val df = DecimalFormat("###,###")
            return df.format(num.toLong())
        }

        fun getDecimalFormat(num: String): String? {

            var number: Long = 0
            return try {

                number = num.toLong()

                val df = DecimalFormat("###,###")
                df.format(number)

            } catch (e: java.lang.Exception) {
                num
            }
        }

        /**
         * JSON 오브젝트 처리
         * @param obj
         * @param name
         * @param defaultValue
         * @return
         */
        fun getJsonValue(obj: JSONObject?, name: String?, defaultValue: String): String? {

            var re = defaultValue
            try {

                if (obj == null) re = defaultValue
                if (name == null) re = defaultValue
                if (defaultValue == null) re = ""

                re = if (obj!!.has(name)) obj.getString(name) else defaultValue

                if (re == "null") re = defaultValue
                if (re == "") re = defaultValue

            } catch (e: java.lang.Exception) {
                // e.printStackTrace()
            }

            return re
        }

        fun getJsonValue(obj: JSONObject?, name: String?, defaultValue: Int): Int {

            var name = name
            var re = defaultValue

            if (name == "") name = "0"

            try {

                if (obj == null) re = defaultValue
                if (name == null) re = defaultValue

                re = if (obj!!.has(name)) obj.getInt(name) else defaultValue

            } catch (e: java.lang.Exception) {
                // e.printStackTrace()
            }

            return re
        }

        fun getJsonValue(obj: JSONObject, name: String?, defaultValue: Float): Float {

            var re = defaultValue.toDouble()
            try {

                re = if (obj.has(name)) obj.getDouble(name) else defaultValue.toDouble()

            } catch (e: java.lang.Exception) {
                // e.printStackTrace()
            }

            return re.toFloat()
        }

        fun getJsonValue(obj: JSONObject, name: String?, defaultValue: Boolean): Boolean {

            var re = defaultValue
            try {

                re = if (obj.has(name)) obj.getBoolean(name) else defaultValue

            } catch (e: java.lang.Exception) {
                // e.printStackTrace()
            }

            return re
        }

        fun getJsonValue(obj: JSONObject, name: String?, defaultValue: JSONArray?): JSONArray? {

            var re = defaultValue
            try {

                re = if (obj.has(name)) obj.getJSONArray(name) else defaultValue

            } catch (e: java.lang.Exception) {
                // e.printStackTrace()
            }

            return re
        }

        fun getJsonValue(obj: JSONObject, name: String?, defaultValue: JSONObject?): JSONObject? {

            var re = defaultValue
            try {

                re = if (obj.has(name)) obj.getJSONObject(name) else defaultValue

            } catch (e: java.lang.Exception) {
                // e.printStackTrace()
            }

            return re
        }
    }
}