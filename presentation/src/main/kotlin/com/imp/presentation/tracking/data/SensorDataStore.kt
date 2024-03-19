package com.imp.presentation.tracking.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.imp.presentation.tracking.data.dao.LightDao
import com.imp.presentation.tracking.data.dao.LocationDao
import com.imp.presentation.tracking.data.dao.ScreenDao
import com.imp.presentation.tracking.data.dao.StepDao
import com.imp.presentation.widget.utils.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * DataStoreUtil
 */
class SensorDataStore {

    companion object {

        var context: Context? = null

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "SENSOR_DATA")

        val KEY_LOCATION_DATA = stringPreferencesKey("location_data")
        val KEY_LIGHT_DATA = stringPreferencesKey("light_data")
        val KEY_STEP_DATA = stringPreferencesKey("step_data")
        val KEY_SCREEN_DATA = stringPreferencesKey("screen_data")
        val KEY_TRACKING_START_TIME = longPreferencesKey("tracking_start_time")

        /**
         * Save Location Data
         *
         * @param latitude
         * @param longitude
         * @param timestamp
         */
        suspend fun saveLocationData(latitude: Double, longitude: Double, timestamp: Long) {

            context!!.dataStore.edit { prefs ->

                prefs[KEY_LOCATION_DATA] = buildString {

                    val locationData = prefs[KEY_LOCATION_DATA]
                    if (locationData?.isEmpty() == false) {

                        append(locationData)
                        append("/")
                    }

                    val data = "$latitude, $longitude, $timestamp"
                    append(data)
                }
            }
        }

        /**
         * Save Light Data
         *
         * @param light
         * @param timestamp
         */
        suspend fun saveLightData(light: Float, timestamp: Long) {

            context!!.dataStore.edit { prefs ->

                prefs[KEY_LIGHT_DATA] = buildString {

                    val lightData = prefs[KEY_LIGHT_DATA]
                    if (lightData?.isEmpty() == false) {

                        append(lightData)
                        append("/")
                    }

                    val data = "$light, $timestamp"
                    append(data)
                }
            }
        }

        /**
         * Save Step Data
         */
        suspend fun saveStepData(step: Int, timestamp: Long) {

            context!!.dataStore.edit { prefs ->

                prefs[KEY_STEP_DATA] = buildString {

                    val stepData = prefs[KEY_STEP_DATA]
                    if (stepData?.isEmpty() == false) {

                        append(stepData)
                        append("/")
                    }

                    val data = "$step, $timestamp"
                    append(data)
                }
            }
        }

        /**
         * Save Step Data
         */
        suspend fun saveScreenData(value: String, timestamp: Long) {

            context!!.dataStore.edit { prefs ->

                prefs[KEY_SCREEN_DATA] = buildString {

                    val stepData = prefs[KEY_SCREEN_DATA]
                    if (stepData?.isEmpty() == false) {

                        append(stepData)
                        append("/")
                    }

                    val data = "$value, $timestamp"
                    append(data)
                }
            }
        }

        /**
         * Save Tracking Start Time
         */
        suspend fun saveTrackingStartTime(timestamp: Long) {

            context!!.dataStore.edit { prefs ->

                prefs[KEY_TRACKING_START_TIME] = timestamp
            }
        }

        /**
         * Get Location Data
         *
         * @return
         */
        suspend fun getLocationData(): Flow<ArrayList<LocationDao>> {

            return context!!.dataStore.data.catch { e ->
                if (e is IOException) {
                    e.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }.map { prefs ->

                val result = ArrayList<LocationDao>()

                val value = prefs[KEY_LOCATION_DATA] ?: ""
                if (value.isNotEmpty()) {

                    val dataList = value.trim().split("/")
                    dataList.forEach { data ->

                        val location = data.trim().split(",")
                        if (location.size >= 3) {

                            result.add(LocationDao(
                                latitude = location[0].trim().toFloat(),
                                longitude = location[1].trim().toFloat(),
                                timestamp = if (location[2].trim().isEmpty()) {
                                    System.currentTimeMillis()
                                } else {
                                    location[2].trim().toLong()
                                }
                            ))
                        }
                    }
                }
                result
            }
        }

        /**
         * Get Light Data
         *
         * @return
         */
        suspend fun getLightData(): Flow<ArrayList<LightDao>> {

            return context!!.dataStore.data.catch { e ->
                if (e is IOException) {
                    e.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }.map { prefs ->

                val result = ArrayList<LightDao>()

                val value = prefs[KEY_LIGHT_DATA] ?: ""
                if (value.isNotEmpty()) {

                    val dataList = value.trim().split("/")
                    dataList.forEach { data ->

                        val light = data.trim().split(",")
                        if (light.size >= 2) {

                            result.add(LightDao(
                                light = light[0].trim().toFloat(),
                                timestamp = if (light[1].trim().isNotEmpty()) {
                                    light[1].trim().toLong()
                                } else {
                                    System.currentTimeMillis()
                                }
                            ))
                        }
                    }
                }
                result
            }
        }

        /**
         * Get Step Data
         *
         * @return
         */
        suspend fun getStepData(): Flow<ArrayList<StepDao>> {

            return context!!.dataStore.data.catch { e ->
                if (e is IOException) {
                    e.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }.map { prefs ->

                val result = ArrayList<StepDao>()

                val value = prefs[KEY_STEP_DATA] ?: ""
                if (value.isNotEmpty()) {

                    val dataList = value.trim().split("/")
                    dataList.forEach { data ->

                        val step = data.trim().split(",")
                        if (step.size >= 2) {

                            result.add(StepDao(
                                step = step[0].trim().toInt(),
                                timestamp = if (step[1].trim().isEmpty()) {
                                    System.currentTimeMillis()
                                } else {
                                    step[1].trim().toLong()
                                }
                            ))
                        }
                    }
                }
                result
            }
        }

        /**
         * Get Screen Time Data
         *
         * @return
         */
        suspend fun getScreenTimeData(): Flow<ArrayList<ScreenDao>> {

            return context!!.dataStore.data.catch { e ->
                if (e is IOException) {
                    e.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }.map { prefs ->

                val result = ArrayList<ScreenDao>()

                val value = prefs[KEY_SCREEN_DATA] ?: ""
                if (value.isNotEmpty()) {

                    val dataList = value.trim().split("/")
                    dataList.forEach { data ->

                        val screen = data.trim().split(",")
                        if (screen.size >= 2) {

                            result.add(ScreenDao(
                                state = screen[0].trim(),
                                timestamp = if (screen[1].trim().isEmpty()) {
                                    System.currentTimeMillis()
                                } else {
                                    screen[1].trim().toLong()
                                }
                            ))
                        }
                    }
                }
                result
            }
        }

        /**
         * Get Tracking Start Time
         *
         * @return
         */
        suspend fun getTrackingStartTime(): Flow<Long> {

            return context!!.dataStore.data.catch { e ->
                if (e is IOException) {
                    e.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }.map { prefs ->

                prefs[KEY_TRACKING_START_TIME] ?: 0L
            }
        }

        /**
         * Remove All Data
         */
        suspend fun removeAllData() {

            context!!.dataStore.edit { prefs ->

                prefs.remove(KEY_LOCATION_DATA)
                prefs.remove(KEY_LIGHT_DATA)
                prefs.remove(KEY_STEP_DATA)
                prefs.remove(KEY_SCREEN_DATA)
                prefs.remove(KEY_TRACKING_START_TIME)
            }
        }

        /**
         * Remove Data
         *
         * @param key
         */
        suspend fun <T> removeData(key: Preferences.Key<T>) {
            context!!.dataStore.edit { prefs ->
                prefs.remove(key)
            }
        }

        /**
         * Remove Hour Data
         *
         * @param hour
         */
        suspend fun removeAllHourData(hour: Int) = withContext(Dispatchers.IO) {

            removeLocationHourData(hour)
            removeLightHourData(hour)
            removeStepHourData(hour)
            removeScreenTimeHourData(hour)
        }

        /**
         * Remove Location Data
         *
         * @param hour
         */
        private suspend fun removeLocationHourData(hour: Int) {

            getLocationData().firstOrNull()?.let { list ->

                var result = ""
                val iterator = list.iterator()
                while (iterator.hasNext()) {

                    val itemIterator = iterator.next()

                    val time = DateUtil.getHour(itemIterator.timestamp)
                    if (time != hour) {

                        val data = "${itemIterator.latitude}, ${itemIterator.longitude}, ${itemIterator.timestamp}"
                        if (result.isNotEmpty()) {
                            result += "/$data"
                        } else {
                            result = data
                        }

                    } else continue
                }

                context!!.dataStore.edit { it[KEY_LOCATION_DATA] = result }
            }
        }

        /**
         * Remove Light Data
         *
         * @param hour
         */
        private suspend fun removeLightHourData(hour: Int) {

            getLightData().firstOrNull()?.let { list ->

                var result = ""
                val iterator = list.iterator()
                while (iterator.hasNext()) {

                    val itemIterator = iterator.next()

                    val time = DateUtil.getHour(itemIterator.timestamp)
                    if (time != hour) {

                        val data = "${itemIterator.light}, ${itemIterator.timestamp}"
                        if (result.isNotEmpty()) {
                            result += "/$data"
                        } else {
                            result = data
                        }

                    } else continue
                }

                context!!.dataStore.edit { it[KEY_LIGHT_DATA] = result }
            }
        }

        /**
         * Remove Step Data
         *
         * @param hour
         */
        private suspend fun removeStepHourData(hour: Int) {

            getStepData().firstOrNull()?.let { list ->

                var result = ""
                val iterator = list.iterator()
                while (iterator.hasNext()) {

                    val itemIterator = iterator.next()

                    val time = DateUtil.getHour(itemIterator.timestamp)
                    if (time != hour) {

                        val data = itemIterator.timestamp.toString()
                        if (result.isNotEmpty()) {
                            result += "/$data"
                        } else {
                            result = data
                        }

                    } else continue
                }

                context!!.dataStore.edit { it[KEY_STEP_DATA] = result }
            }
        }

        /**
         * Remove ScreenTime Data
         *
         * @param hour
         */
        private suspend fun removeScreenTimeHourData(hour: Int) {

            getScreenTimeData().firstOrNull()?.let { list ->

                var result = ""
                val iterator = list.iterator()
                while (iterator.hasNext()) {

                    val itemIterator = iterator.next()

                    val time = DateUtil.getHour(itemIterator.timestamp)
                    if (time != hour) {

                        val data = "${itemIterator.state}, ${itemIterator.timestamp}"
                        if (result.isNotEmpty()) {
                            result += "/$data"
                        } else {
                            result = data
                        }

                    } else continue
                }

                context!!.dataStore.edit { it[KEY_SCREEN_DATA] = result }
            }
        }
    }
}