package com.imp.presentation.tracking.util

import android.content.Context
import android.util.Log
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.tracking.data.SensorDataStore
import com.imp.presentation.tracking.data.dao.LightDao
import com.imp.presentation.widget.utils.DateUtil
import com.imp.presentation.widget.utils.MethodStorageUtil
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar
import kotlin.math.roundToInt

/**
 * Tracking Data Util
 */
class TrackingDataUtil {

    companion object {

        private const val TAG = "work"

        /**
         * Remove Data And Reset Tracking Start Time
         *
         * @param timestamp
         */
        suspend fun removeData(timestamp: Long) {

            // remove all data
            val hour = DateUtil.getHour(timestamp)
            SensorDataStore.removeAllHourData(hour)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = timestamp
                add(Calendar.HOUR_OF_DAY, 1)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            // tracking start time 초기화
            SensorDataStore.saveTrackingStartTime(calendar.timeInMillis)
            Log.d(TAG, "reset start time: ${DateUtil.getLocaleDate(calendar.timeInMillis)}")
        }

        /**
         * Get Illuminance
         *
         * @return first timestamp, illuminance list
         */
        suspend fun getIlluminance(): Pair<Long, ArrayList<Int>> {

            var timestamp = System.currentTimeMillis()
            val result: ArrayList<Int> = ArrayList()

            SensorDataStore.getLightData().firstOrNull()?.let { lightList ->

                // Return when list is empty
                if (lightList.isEmpty()) {

                    Log.d(TAG, "getIlluminance return")
                    return Pair(timestamp, ArrayList())
                }

                timestamp = lightList.first().timestamp

                val hour = DateUtil.getLocaleDate(lightList.first().timestamp).hour
                val filteredList = lightList.filter {

                    val itemHour = DateUtil.getLocaleDate(it.timestamp).hour
                    hour == itemHour
                }

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
         * @return first timestamp, pedometer
         */
        suspend fun getPedometer(): Pair<Long, Int> {

            var timestamp = System.currentTimeMillis()
            var result = 0

            SensorDataStore.getStepData().firstOrNull()?.let { stepList ->

                // Return when list is empty
                if (stepList.isEmpty()) {

                    Log.d(TAG, "getPedometer return")
                    return Pair(timestamp, 0)
                }

                timestamp = stepList.first().timestamp

                val hour = DateUtil.getLocaleDate(stepList.first().timestamp).hour
                val filteredList = stepList.filter {

                    val itemHour = DateUtil.getLocaleDate(it.timestamp).hour
                    hour == itemHour
                }

                result = filteredList.size
            }

            Log.d(TAG, "stepList: $result")

            return Pair(timestamp, result)
        }

        /**
         * Get GPS
         *
         * @return first timestamp, Gps[latitude, longitude, timestamp] list
         */
        suspend fun getGps(): Pair<Long, ArrayList<Array<Double>>> {

            var timestamp = System.currentTimeMillis()
            val result: ArrayList<Array<Double>> = ArrayList()

            SensorDataStore.getLocationData().firstOrNull()?.let { locationList ->

                // Return when list is empty
                if (locationList.isEmpty()) {

                    // todo: 데이터가 없을 경우, 현재 좌표 가져오기
                    Log.d(TAG, "getGps return")
                    return Pair(timestamp, ArrayList())
                }

                timestamp = locationList.first().timestamp

                val hour = DateUtil.getLocaleDate(locationList.first().timestamp).hour
                val filteredList = locationList.filter {

                    val itemHour = DateUtil.getLocaleDate(it.timestamp).hour
                    hour == itemHour
                }

                filteredList.forEachIndexed { index, dao ->

                    val minute: Int

                    if (index + 1 < filteredList.size) {

                        // 마지막 좌표가 아닌 경우, 다음 좌표와 머무른 시간 계산
                        val nextIterator = filteredList[index + 1]
                        minute = DateUtil.getMin(nextIterator.timestamp) - DateUtil.getMin(dao.timestamp)
                        Log.d(TAG, "gps iterator.hasNext() -> ${DateUtil.getMin(nextIterator.timestamp)} - ${DateUtil.getMin(dao.timestamp)} = $minute")

                    } else {

                        // 마지막 좌표인 경우, 60분 or 서버에 저장할 시점의 시간과 계산
                        val currentTimestamp = System.currentTimeMillis()
                        val maxMin = if (currentTimestamp > locationList.first().timestamp && DateUtil.getHour(currentTimestamp) != hour) {
                            60
                        } else {
                            DateUtil.getMin(currentTimestamp)
                        }

                        minute = maxMin - DateUtil.getMin(dao.timestamp)
                        Log.d(TAG, "gpe else -> $maxMin - ${DateUtil.getMin(dao.timestamp)} = $minute")
                    }

                    result.add(arrayOf(
                        dao.latitude.toDouble(),
                        dao.longitude.toDouble(),
                        minute.toDouble()
                    ))
                }
                Log.d(TAG, "gpsList: $result")
            }

            var str = ""
            result.forEach { str = if (str.isEmpty()) "${it[0]}, ${it[1]}, ${it[2]}" else "$str / ${it[0]}, ${it[1]}, ${it[2]}" }
            Log.d(TAG, "gpsList: $str")

            return Pair(timestamp, result)
        }

        /**
         * Get Screen Time
         *
         * @return first timestamp, screen time(min), screen frequency
         */
        suspend fun getScreenTime(context: Context): Triple<Long, Int, Int> {

            var timestamp = System.currentTimeMillis()
            var screenTime = 0L
            var screenFrequency = 0

            SensorDataStore.getScreenTimeData().firstOrNull()?.let { screenTimeList ->

                val startTrackingTimestamp = SensorDataStore.getTrackingStartTime().firstOrNull() ?: 0L
                Log.d(TAG, "startTrackingTimestamp: ${DateUtil.getLocaleDate(startTrackingTimestamp)}")

                // todo
                // Return when list is empty
                if (screenTimeList.isEmpty()) {

                    val maxMin = if (timestamp > startTrackingTimestamp && DateUtil.getHour(timestamp) != DateUtil.getHour(startTrackingTimestamp)) {
                        60
                    } else {
                        DateUtil.getMin(timestamp)
                    }

                    val totalScreenTime = if (MethodStorageUtil.checkDeviceStatus(context)) maxMin - DateUtil.getMin(startTrackingTimestamp) else 0
                    Log.d(TAG, "getScreenTime is $totalScreenTime")
                    return Triple(-1L, totalScreenTime, 0)
                }

                timestamp = screenTimeList.first().timestamp

                val hour = DateUtil.getLocaleDate(screenTimeList.first().timestamp).hour
                val filteredList = screenTimeList.filter {

                    val itemHour = DateUtil.getLocaleDate(it.timestamp).hour
                    hour == itemHour
                }
                Log.d(TAG, "ScreenTime hour: $hour")

                // 화면 깨운 횟수 저장
                screenFrequency = filteredList.filter { it.state.equals(BaseConstants.SCREEN_STATE_ON, ignoreCase = true) }.size

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = filteredList.firstOrNull()?.timestamp ?: 0L
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                var screenOnTimestamp = if (startTrackingTimestamp == 0L ||
                    startTrackingTimestamp > (filteredList.firstOrNull()?.timestamp ?: 0L)) {
                    calendar.timeInMillis
                } else {
                    startTrackingTimestamp
                }

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

                    val currentTimestamp = System.currentTimeMillis()

                    calendar.apply {
                        timeInMillis = filteredList.firstOrNull()?.timestamp ?: 0L
                        add(Calendar.HOUR_OF_DAY, 1)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }

                    val maxTimeStamp = if (currentTimestamp > calendar.timeInMillis) {
                        calendar.timeInMillis
                    } else {
                        currentTimestamp
                    }

                    // 서버에 데이터를 저장하는 시점에 마지막 데이터가 ON이라면, (-> 핸드폰을 계속 켜둔 상태)
                    // 현재 시간과 비교해서 더 이전일 경우에 screen time 추가
                    if (last.state.equals(BaseConstants.SCREEN_STATE_ON, ignoreCase = true)
                        && last.timestamp < maxTimeStamp) {

                        screenTime += maxTimeStamp - last.timestamp
                    }
                }
            }

            Log.d(TAG, "screenTime: ${DateUtil.getMin(screenTime)}, screenFrequency: $screenFrequency")

            return Triple(timestamp, DateUtil.getMin(screenTime), screenFrequency)
        }
    }
}