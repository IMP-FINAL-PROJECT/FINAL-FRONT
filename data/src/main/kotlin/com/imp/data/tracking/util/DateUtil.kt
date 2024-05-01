package com.imp.data.tracking.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
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

        /**
         * String (yyyy-MM-dd) to LocalDate
         */
        fun stringToLocalDate(string: String): LocalDate {

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return LocalDate.parse(string, formatter)
        }

        /**
         * Get Locale Date
         */
        fun getLocaleDate(timestamp: Long): LocalDateTime {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        }

        /**
         * Get Hour
         */
        fun getHour(timestamp: Long): Int {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).hour
        }

        /**
         * Get Minute
         */
        fun getMin(timestamp: Long): Int {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).minute
        }

        /**
         * Timestamp to Data
         */
        @SuppressLint("SimpleDateFormat")
        fun timestampToData(timestamp: Long): String {

            val timestamp = if (timestamp == 0L) System.currentTimeMillis() else timestamp
            return SimpleDateFormat("yyyy-MM-dd").format(Date(timestamp))
        }

        /**
         * Timestamp to Data
         */
        @SuppressLint("SimpleDateFormat")
        fun timestampToDataMin(timestamp: Long): String {

            val timestamp = if (timestamp == 0L) System.currentTimeMillis() else timestamp
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(timestamp))
        }
    }
}