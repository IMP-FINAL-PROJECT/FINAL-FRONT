package com.imp.presentation.widget.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.DayOfWeek
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
         * Get Current Date
         */
        fun getCurrentDateWithText(pattern: String): String {

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())

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
         * Get Current Weekly (MM월 dd일 ~ MM월 dd일)
         */
        fun getCurrentWeekly(): String {

            val today = LocalDate.now()
            val startOfWeek = today.with(DayOfWeek.SUNDAY).minusDays(7)
            val endOfWeek = startOfWeek.plusDays(6)

            val formatter = DateTimeFormatter.ofPattern("MM월 dd일")

            val formattedStartOfWeek = startOfWeek.format(formatter)
            val formattedEndOfWeek = endOfWeek.format(formatter)

            return "$formattedStartOfWeek ~ $formattedEndOfWeek"
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
        fun timestampToTimeSeconds(timestamp: Long): String {

            val instant = Instant.ofEpochMilli(timestamp)
            val dateTime = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

            return formatter.format(dateTime)
        }

        /**
         * Timestamp to Time (HH:mm:ss)
         */
        fun timestampToTimeMin(timestamp: Long): String {

            val instant = Instant.ofEpochMilli(timestamp)
            val dateTime = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())
            val formatter = DateTimeFormatter.ofPattern("HH:mm")

            return formatter.format(dateTime)
        }

        /**
         * Timestamp to Screen Time (hour, minute)
         */
        fun timestampToScreenTime(timestamp: Long): Pair<Int, Int> {

            val timestampSeconds = timestamp / 1000
            val hour = timestampSeconds / 3600
            val minute = (timestampSeconds % 3600) / 60

            return Pair(hour.toInt(), minute.toInt())
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

        /**
         * String to (yyyy-MM-dd) String
         */
        @SuppressLint("SimpleDateFormat")
        fun stringToDate(date: String): String {

            return try {
                SimpleDateFormat("yyyy-MM-dd").parse(date).toString()
            } catch (e: Exception) {
                e.printStackTrace()
                getCurrentDateWithText("yyyy-MM-dd")
            }
        }
    }
}