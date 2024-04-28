package com.imp.data.tracking.util

import android.content.Context
import android.util.Log
import com.imp.data.tracking.constants.BaseConstants
import com.imp.data.tracking.data.SensorDataStore
import com.imp.data.tracking.data.dao.LightDao
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar
import kotlin.math.roundToInt

/**
 * Tracking Data Util
 */
class TrackingDataUtil {

    companion object {

        private const val TAG = "tracking"

        /**
         * Remove Data And Reset Tracking Start Time
         *
         * @param context
         * @param timestamp
         */
        suspend fun removeData(context: Context, timestamp: Long): Boolean {

            // remove all data
            val hour = DateUtil.getHour(timestamp)
            SensorDataStore.removeAllHourData(context, hour)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = timestamp
                add(Calendar.HOUR_OF_DAY, 1)
                resetCalendarTime()
            }

            // tracking start time 초기화
            SensorDataStore.saveTrackingStartTime(context, calendar.timeInMillis)
            Log.d(TAG, "reset start time: ${DateUtil.getLocaleDate(calendar.timeInMillis)}")

            return true
        }

        /**
         * Get Illuminance
         *
         * @param context
         * @return first timestamp, illuminance list
         */
        suspend fun getIlluminance(context: Context): Pair<Long, ArrayList<Int>> {
            Log.d(TAG, "<------- Get Illuminance ------>")

            var timestamp = System.currentTimeMillis()
            val result: ArrayList<Int> = ArrayList()

            SensorDataStore.getLightData(context).firstOrNull()?.let { lightList ->

                // Return when list is empty
                if (lightList.isEmpty()) {

                    Log.d(TAG, "getIlluminance return")
                    return Pair(timestamp, ArrayList())
                }

                timestamp = lightList.first().timestamp

                val hour = DateUtil.getLocaleDate(lightList.first().timestamp).hour
                val filteredList = lightList.filter { hour == DateUtil.getLocaleDate(it.timestamp).hour }

                val minuteList: ArrayList<LightDao> = ArrayList()
                var minute = DateUtil.getLocaleDate(filteredList.firstOrNull()?.timestamp?: 0L).minute

                val iterator = filteredList.iterator()
                while (iterator.hasNext()) {

                    val itemIterator = iterator.next()
                    val itemMin = DateUtil.getLocaleDate(itemIterator.timestamp).minute

                    if (minute == itemMin) {

                        // 현재 저장 중인 시간(분)과 동일할 경우 값 저장
                        minuteList.add(itemIterator)

                    } else {

                        // 지금까지 저장되어 있는 값 평균
                        val avg = minuteList.sumOf { it.light.roundToInt() } / minuteList.size
                        result.add(avg)
                        //Log.d(TAG, "minute: $minute, size: ${minuteList.size}")

                        // 초기화
                        minuteList.clear()

                        // 값 저장
                        minute = itemMin
                        minuteList.add(itemIterator)
                    }
                }
            }

            Log.d(TAG, "lightList: $result")

            return Pair(timestamp, result)
        }

        /**
         * Get Pedometer
         *
         * @param context
         * @return first timestamp, pedometer
         */
        suspend fun getPedometer(context: Context): Pair<Long, Int> {
            Log.d(TAG, "<------- Get Pedometer ------>")

            var timestamp = System.currentTimeMillis()
            var result = 0

            SensorDataStore.getStepData(context).firstOrNull()?.let { stepList ->

                // Return when list is empty
                if (stepList.isEmpty()) {

                    Log.d(TAG, "getPedometer return")
                    return Pair(timestamp, 0)
                }

                timestamp = stepList.first().timestamp

                val hour = DateUtil.getLocaleDate(stepList.first().timestamp).hour
                val filteredList = stepList.filter { hour == DateUtil.getLocaleDate(it.timestamp).hour }

                result = filteredList.size
            }

            Log.d(TAG, "stepList: $result")

            return Pair(timestamp, result)
        }

        /**
         * Get GPS
         *
         * @param context
         * @return first timestamp, Gps[latitude, longitude, timestamp] list
         */
        suspend fun getGps(context: Context): Pair<Long, ArrayList<Array<Double>>> {
            Log.d(TAG, "<------- Get GPS ------>")

            var timestamp = System.currentTimeMillis()
            val result: ArrayList<Array<Double>> = ArrayList()

            SensorDataStore.getLocationData(context).firstOrNull()?.let { locationList ->

                val startTrackingTimestamp = SensorDataStore.getTrackingStartTime(context).firstOrNull() ?: 0L
                Log.d(TAG, "startTrackingTimestamp: ${DateUtil.getLocaleDate(startTrackingTimestamp)}")

                val startToEndCalendar = Calendar.getInstance().apply {
                    timeInMillis = startTrackingTimestamp
                    add(Calendar.HOUR_OF_DAY, 1)
                    resetCalendarTime()
                }

                var firstGps = SensorDataStore.getRecentGps(context).firstOrNull()
                if (firstGps?.first == 0.0 || firstGps?.second == 0.0) {
                    firstGps = null
                }

                // Return when list is empty
                if (locationList.isEmpty()) {

                    firstGps?.let { gps ->

                        val currentCalendar = Calendar.getInstance()
                        Log.d(TAG, "current: ${DateUtil.getLocaleDate(currentCalendar.timeInMillis)}, start: ${DateUtil.getLocaleDate(startToEndCalendar.timeInMillis)}")
                        val maxTimestamp = if (currentCalendar.timeInMillis > startToEndCalendar.timeInMillis) {
                            startToEndCalendar.timeInMillis
                        } else {
                            currentCalendar.timeInMillis
                        }

                        Log.d(TAG, "${DateUtil.getLocaleDate(maxTimestamp)} - ${DateUtil.getLocaleDate(startTrackingTimestamp)} = ${DateUtil.getLocaleDate(maxTimestamp - startTrackingTimestamp)}")
                        result.add(arrayOf(gps.first, gps.second, (maxTimestamp - startTrackingTimestamp).toDouble()))
                    }

                    Log.d(TAG, "getGps return")
                    return Pair(startTrackingTimestamp, result)
                }

                timestamp = locationList.first().timestamp

                val hour = DateUtil.getLocaleDate(locationList.first().timestamp).hour
                val filteredList = locationList.filter { hour == DateUtil.getLocaleDate(it.timestamp).hour }

                if (firstGps != null && filteredList.isNotEmpty()) {

                    // 이전 좌표가 있는 경우, 이전 좌표 ~ 첫 번째 좌표 사이의 머무른 시간 계산
                    val nextItem = filteredList.first()
                    val millisecond = nextItem.timestamp - startTrackingTimestamp
                    if (millisecond > 0) {

                        Log.d(TAG, "firstGps -> ${DateUtil.getMin(nextItem.timestamp)} - ${DateUtil.getMin(startTrackingTimestamp)} = ${DateUtil.getMin(millisecond)}")

                        result.add(arrayOf(
                            firstGps.first,
                            firstGps.second,
                            millisecond.toDouble()
                        ))
                    }
                }

                filteredList.forEachIndexed { index, dao ->

                    val minute: Long

                    if (index + 1 < filteredList.size) {

                        // 마지막 좌표가 아닌 경우, 다음 좌표와 머무른 시간 계산
                        val nextIterator = filteredList[index + 1]
                        minute = nextIterator.timestamp - dao.timestamp
                        Log.d(TAG, "gps iterator.hasNext() -> ${DateUtil.getMin(nextIterator.timestamp)} - ${DateUtil.getMin(dao.timestamp)} = ${DateUtil.getMin(minute)}")

                    } else {

                        // 마지막 좌표인 경우, 60분 or 서버에 저장할 시점의 시간과 계산
                        val currentCalendar = Calendar.getInstance()
                        val maxMin = if (currentCalendar.timeInMillis > startToEndCalendar.timeInMillis) {
                            startToEndCalendar.timeInMillis
                        } else {
                            currentCalendar.timeInMillis
                        }

                        Log.d(TAG, "dao.timestamp: ${DateUtil.getLocaleDate(dao.timestamp)}")
                        minute = maxMin - dao.timestamp
                        Log.d(TAG, "gpe else -> ${DateUtil.getMin(maxMin)} - ${DateUtil.getMin(dao.timestamp)} = ${DateUtil.getMin(minute)}")
                    }

                    result.add(arrayOf(
                        dao.latitude.toDouble(),
                        dao.longitude.toDouble(),
                        minute.toDouble()
                    ))
                }
            }

            var str = ""
            result.forEach { str = if (str.isEmpty()) "${it[0]}, ${it[1]}, ${it[2]} = ${DateUtil.getMin(it[2].toLong())}" else "$str / ${it[0]}, ${it[1]}, ${it[2]} = ${DateUtil.getMin(it[2].toLong())}" }
            Log.d(TAG, "gpsList: $str")

            return Pair(timestamp, result)
        }

        /**
         * Get Screen Time
         *
         * @param context
         * @return first timestamp, screen time(millisecond), screen frequency
         */
        suspend fun getScreenTime(context: Context): Triple<Long, Int, Int> {
            Log.d(TAG, "<------- Get Screen Time ------>")

            var timestamp = System.currentTimeMillis()
            var screenTime = 0L
            var screenFrequency = 0

            SensorDataStore.getScreenTimeData(context).firstOrNull()?.let { screenTimeList ->

                val startTrackingTimestamp = SensorDataStore.getTrackingStartTime(context).firstOrNull() ?: 0L
                Log.d(TAG, "startTrackingTimestamp: ${DateUtil.getLocaleDate(startTrackingTimestamp)}")

                // Return when list is empty
                if (screenTimeList.isEmpty()) {

                    val currentCalendar = Calendar.getInstance()
                    val startCalendar = Calendar.getInstance().apply {
                        timeInMillis = startTrackingTimestamp
                        add(Calendar.HOUR_OF_DAY, 1)
                        resetCalendarTime()
                    }

                    Log.d(TAG, "current: ${DateUtil.getLocaleDate(currentCalendar.timeInMillis)}, start: ${DateUtil.getLocaleDate(startCalendar.timeInMillis)}")
                    val maxTimestamp = if (currentCalendar.timeInMillis > startCalendar.timeInMillis) {
                        startCalendar.timeInMillis
                    } else {
                        currentCalendar.timeInMillis
                    }

                    val totalScreenTime = if (MethodStorageUtil.checkDeviceStatus(context)) maxTimestamp - startTrackingTimestamp else 0
                    Log.d(TAG, "getScreenTime is $totalScreenTime")
                    return Triple(startTrackingTimestamp, totalScreenTime.toInt(), 0)
                }

                timestamp = screenTimeList.first().timestamp

                val hour = DateUtil.getLocaleDate(screenTimeList.first().timestamp).hour
                val filteredList = screenTimeList.filter { hour == DateUtil.getLocaleDate(it.timestamp).hour }

                // 화면 깨운 횟수 저장
                screenFrequency = filteredList.filter { it.state.equals(BaseConstants.SCREEN_STATE_ON, ignoreCase = true) }.size

                var screenOnTimestamp = startTrackingTimestamp

                val iterator = filteredList.listIterator()
                while (iterator.hasNext()) {

                    val itemIterator = iterator.next()
                    when(itemIterator.state) {

                        BaseConstants.SCREEN_STATE_ON -> {

                            // 화면을 깨운 시간 임시 저장
                            screenOnTimestamp = itemIterator.timestamp
                        }

                        BaseConstants.SCREEN_STATE_OFF -> {

                            // 스크린 타임 계산
                            screenTime += itemIterator.timestamp - screenOnTimestamp
                            Log.d(TAG, "${DateUtil.getLocaleDate(itemIterator.timestamp)} - ${DateUtil.getLocaleDate(screenOnTimestamp)}: ${itemIterator.timestamp - screenOnTimestamp}")
                        }
                    }
                }

                filteredList.lastOrNull()?.let { last ->

                    // 서버에 데이터를 저장하는 시점에 마지막 데이터가 ON이라면, (-> 핸드폰을 계속 켜둔 상태)
                    // 현재 시간과 비교해서 더 이전일 경우에 screen time 추가
                    if (last.state.equals(BaseConstants.SCREEN_STATE_ON, ignoreCase = true)) {

                        val currentCalendar = Calendar.getInstance()
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = filteredList.firstOrNull()?.timestamp ?: 0L
                            add(Calendar.HOUR_OF_DAY, 1)
                            resetCalendarTime()
                        }

                        val maxTimeStamp = if (currentCalendar.timeInMillis > calendar.timeInMillis) {
                            calendar.timeInMillis
                        } else {
                            currentCalendar.timeInMillis
                        }

                        if (last.timestamp < maxTimeStamp) {

                            screenTime += maxTimeStamp - last.timestamp
                            Log.d(TAG, "${DateUtil.getLocaleDate(maxTimeStamp)} - ${DateUtil.getLocaleDate(last.timestamp)}: ${maxTimeStamp - last.timestamp}")
                        }
                    }
                }
            }

            Log.d(TAG, "screenTime: $screenTime, screenFrequency: $screenFrequency")

            return Triple(timestamp, screenTime.toInt(), screenFrequency)
        }

        /**
         * Get Phone Call
         *
         * @param context
         * @return first timestamp, call duration(millisecond), call frequency
         */
        suspend fun getPhoneCall(context: Context): Triple<Long, Int, Int> {
            Log.d(TAG, "<------- Get Phone Call ------>")

            var timestamp = System.currentTimeMillis()
            var callDuration = 0L
            var callFrequency = 0

            SensorDataStore.getPhoneCallData(context).firstOrNull()?.let { phoneCallList ->

                val startTrackingTimestamp = SensorDataStore.getTrackingStartTime(context).firstOrNull() ?: 0L
                Log.d(TAG, "startTrackingTimestamp: ${DateUtil.getLocaleDate(startTrackingTimestamp)}")

                // Return when list is empty
                if (phoneCallList.isEmpty()) {

                    val currentCalendar = Calendar.getInstance()
                    val startCalendar = Calendar.getInstance().apply {
                        timeInMillis = startTrackingTimestamp
                        add(Calendar.HOUR_OF_DAY, 1)
                        resetCalendarTime()
                    }

                    Log.d(TAG, "current: ${DateUtil.getLocaleDate(currentCalendar.timeInMillis)}, start: ${DateUtil.getLocaleDate(startCalendar.timeInMillis)}")
                    val maxTimestamp = if (currentCalendar.timeInMillis > startCalendar.timeInMillis) {
                        startCalendar.timeInMillis
                    } else {
                        currentCalendar.timeInMillis
                    }

                    SensorDataStore.getCurrentCallState(context).firstOrNull()?.let { callState ->

                        val totalCallTime = if (callState) maxTimestamp - startTrackingTimestamp else 0
                        Log.d(TAG, "getPhoneCall is $totalCallTime")
                        return Triple(startTrackingTimestamp, totalCallTime.toInt(), 0)
                    }
                }

                timestamp = phoneCallList.first().timestamp

                val hour = DateUtil.getLocaleDate(phoneCallList.first().timestamp).hour
                val filteredList = phoneCallList.filter { hour == DateUtil.getLocaleDate(it.timestamp).hour }

                // 통화 횟수 저장
                callFrequency = filteredList.filter { it.state.equals(BaseConstants.PHONE_CALL_STATE_ON, ignoreCase = true) }.size

                var callStartTimestamp = startTrackingTimestamp

                val iterator = filteredList.listIterator()
                while (iterator.hasNext()) {

                    val itemIterator = iterator.next()
                    when(itemIterator.state) {

                        BaseConstants.PHONE_CALL_STATE_ON -> {

                            // 통화 시작 시간 임시 저장
                            callStartTimestamp = itemIterator.timestamp
                        }

                        BaseConstants.PHONE_CALL_STATE_OFF -> {

                            // 통화 시간 계산
                            callDuration += itemIterator.timestamp - callStartTimestamp
                            Log.d(TAG, "${DateUtil.getLocaleDate(itemIterator.timestamp)} - ${DateUtil.getLocaleDate(callStartTimestamp)}: ${itemIterator.timestamp - callStartTimestamp}")
                        }
                    }
                }

                filteredList.lastOrNull()?.let { last ->

                    // 서버에 데이터를 저장하는 시점에 마지막 데이터가 ON이라면, (-> 계속 전화 중인 상태)
                    // 현재 시간과 비교해서 더 이전일 경우에 통화 시간 추가
                    if (last.state.equals(BaseConstants.PHONE_CALL_STATE_ON, ignoreCase = true)) {

                        val currentCalendar = Calendar.getInstance()
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = filteredList.firstOrNull()?.timestamp ?: 0L
                            add(Calendar.HOUR_OF_DAY, 1)
                            resetCalendarTime()
                        }

                        val maxTimeStamp = if (currentCalendar.timeInMillis > calendar.timeInMillis) {
                            calendar.timeInMillis
                        } else {
                            currentCalendar.timeInMillis
                        }

                        if (last.timestamp < maxTimeStamp) {

                            callDuration += maxTimeStamp - last.timestamp
                            Log.d(TAG, "${DateUtil.getLocaleDate(maxTimeStamp)} - ${DateUtil.getLocaleDate(last.timestamp)}: ${maxTimeStamp - last.timestamp}")
                        }
                    }
                }
            }

            Log.d(TAG, "callDuration: $callDuration, callFrequency: $callFrequency")

            return Triple(timestamp, callDuration.toInt(), callFrequency)
        }
    }
}