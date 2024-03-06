package com.imp.presentation.widget.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Date Util
 */
class DateUtil {

    companion object {

        /**
         * Get Current Date (yyyyMMdd)
         */
        fun getCurrentDate(): String {

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

            return dateFormat.format(calendar.time)
        }

        /**
         * Get Current Date (오늘, MM월 dd일)
         */
        fun getCurrentMonthDay(): String {

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("오늘, MM월 dd일", Locale.getDefault())

            return dateFormat.format(calendar.time)
        }

        /**
         * Get Current Time (HH:mm:ss)
         */
        fun getCurrentTime(): String {

            val calendar = Calendar.getInstance()
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

            return timeFormat.format(calendar.time)
        }

        /**
         * Timestamp to Time (HH:mm:ss)
         */
        fun timestampToTime(timestamp: Long): String {

            val instant = Instant.ofEpochMilli(timestamp)
            val dateTime = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

            return formatter.format(dateTime)
        }
    }
}