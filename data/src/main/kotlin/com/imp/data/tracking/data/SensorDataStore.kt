package com.imp.data.tracking.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.imp.data.tracking.data.dao.LightDao
import com.imp.data.tracking.data.dao.LocationDao
import com.imp.data.tracking.data.dao.ScreenDao
import com.imp.data.tracking.data.dao.StepDao
import com.imp.data.tracking.util.DateUtil
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

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "SENSOR_DATA")

        private val KEY_LOCATION_DATA = stringPreferencesKey("location_data")
        private val KEY_LIGHT_DATA = stringPreferencesKey("light_data")
        private val KEY_STEP_DATA = stringPreferencesKey("step_data")
        private val KEY_SCREEN_DATA = stringPreferencesKey("screen_data")
        private val KEY_PHONE_CALL_DATA = stringPreferencesKey("phone_call_data")
        private val KEY_TRACKING_START_TIME = longPreferencesKey("tracking_start_time")
        private val KEY_RECENT_GPS = stringPreferencesKey("recent_gps")
        private val KEY_CURRENT_CALL_STATE = booleanPreferencesKey("current_call_state")

        /**
         * Save Location Data
         *
         * @param context
         * @param latitude
         * @param longitude
         * @param timestamp
         */
        suspend fun saveLocationData(context: Context, latitude: Double, longitude: Double, timestamp: Long) {

            context.applicationContext.dataStore.edit { prefs ->

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
         * @param context
         * @param light
         * @param timestamp
         */
        suspend fun saveLightData(context: Context, light: Float, timestamp: Long) {

            context.applicationContext.dataStore.edit { prefs ->

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
         *
         * @param context
         * @param step
         * @param timestamp
         */
        suspend fun saveStepData(context: Context, step: Int, timestamp: Long) {

            context.applicationContext.dataStore.edit { prefs ->

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
         * Save Screen Data
         *
         * @param context
         * @param value
         * @param timestamp
         */
        suspend fun saveScreenData(context: Context, value: String, timestamp: Long) {

            context.applicationContext.dataStore.edit { prefs ->

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
         * Save Phone Call Data
         *
         * @param context
         * @param value
         * @param timestamp
         */
        suspend fun savePhoneCallData(context: Context, value: String, timestamp: Long) {

            context.applicationContext.dataStore.edit { prefs ->

                prefs[KEY_PHONE_CALL_DATA] = buildString {

                    val stepData = prefs[KEY_PHONE_CALL_DATA]
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
         *
         * @param context
         * @param timestamp
         */
        suspend fun saveTrackingStartTime(context: Context, timestamp: Long): Boolean {

            context.applicationContext.dataStore.edit { prefs ->

                prefs[KEY_TRACKING_START_TIME] = timestamp
            }

            return true
        }

        /**
         * Save Recent Location Data
         *
         * @param context
         * @param latitude
         * @param longitude
         */
        suspend fun saveRecentGps(context: Context, latitude: Double, longitude: Double) {

            context.applicationContext.dataStore.edit { prefs ->

                prefs[KEY_RECENT_GPS] = "$latitude, $longitude"
            }
        }

        /**
         * Save Current Call State
         *
         * @param context
         * @param isOn
         */
        suspend fun saveCurrentCallState(context: Context, isOn: Boolean) {

            context.applicationContext.dataStore.edit { prefs ->

                prefs[KEY_CURRENT_CALL_STATE] = isOn
            }
        }

        /**
         * Get Location Data
         *
         * @param context
         * @return
         */
        suspend fun getLocationData(context: Context): Flow<ArrayList<LocationDao>> {

            return context.applicationContext.dataStore.data.catch { e ->
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
                                timestamp = location[2].trim().toLongOrNull() ?: System.currentTimeMillis()
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
         * @param context
         * @return
         */
        suspend fun getLightData(context: Context): Flow<ArrayList<LightDao>> {

            return context.applicationContext.dataStore.data.catch { e ->
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
                                timestamp = light[1].trim().toLongOrNull() ?: System.currentTimeMillis()
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
         * @param context
         * @return
         */
        suspend fun getStepData(context: Context): Flow<ArrayList<StepDao>> {

            return context.applicationContext.dataStore.data.catch { e ->
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
                                timestamp = step[1].trim().toLongOrNull() ?: System.currentTimeMillis()
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
         * @param context
         * @return
         */
        suspend fun getScreenTimeData(context: Context): Flow<ArrayList<ScreenDao>> {

            return context.applicationContext.dataStore.data.catch { e ->
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
                                timestamp = screen[1].trim().toLongOrNull() ?: System.currentTimeMillis()
                            ))
                        }
                    }
                }
                result
            }
        }

        /**
         * Get Phone Call Data
         *
         * @param context
         * @return
         */
        suspend fun getPhoneCallData(context: Context): Flow<ArrayList<ScreenDao>> {

            return context.applicationContext.dataStore.data.catch { e ->
                if (e is IOException) {
                    e.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }.map { prefs ->

                val result = ArrayList<ScreenDao>()

                val value = prefs[KEY_PHONE_CALL_DATA] ?: ""
                if (value.isNotEmpty()) {

                    val dataList = value.trim().split("/")
                    dataList.forEach { data ->

                        val call = data.trim().split(",")
                        if (call.size >= 2) {

                            result.add(ScreenDao(
                                state = call[0].trim(),
                                timestamp = call[1].trim().toLongOrNull() ?: System.currentTimeMillis()
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
         * @param context
         * @return
         */
        suspend fun getTrackingStartTime(context: Context): Flow<Long> {

            return context.applicationContext.dataStore.data.catch { e ->
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
         * Get Tracking Start Time
         *
         * @param context
         * @return
         */
        suspend fun getRecentGps(context: Context): Flow<Pair<Double, Double>> {

            return context.applicationContext.dataStore.data.catch { e ->
                if (e is IOException) {
                    e.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }.map { prefs ->

                var result = Pair(0.0, 0.0)

                val value = prefs[KEY_RECENT_GPS] ?: ""
                if (value.isNotEmpty()) {

                    val screen = value.trim().split(",")
                    if (screen.size >= 2) {

                        result = Pair(screen[0].toDouble(), screen[1].toDouble())
                    }
                }

                result
            }
        }

        /**
         * Get Current Call State
         *
         * @param context
         * @return
         */
        suspend fun getCurrentCallState(context: Context): Flow<Boolean> {

            return context.applicationContext.dataStore.data.catch { e ->
                if (e is IOException) {
                    e.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }.map { prefs ->

                prefs[KEY_CURRENT_CALL_STATE] ?: false
            }
        }

        /**
         * Remove All Data
         */
        suspend fun removeAllData(context: Context) {

            context.applicationContext.dataStore.edit { prefs ->

                prefs.remove(KEY_LOCATION_DATA)
                prefs.remove(KEY_LIGHT_DATA)
                prefs.remove(KEY_STEP_DATA)
                prefs.remove(KEY_SCREEN_DATA)
                prefs.remove(KEY_PHONE_CALL_DATA)
                prefs.remove(KEY_TRACKING_START_TIME)
                prefs.remove(KEY_RECENT_GPS)
                prefs.remove(KEY_CURRENT_CALL_STATE)
            }
        }

        /**
         * Remove Data
         *
         * @param context
         * @param key
         */
        suspend fun <T> removeData(context: Context, key: Preferences.Key<T>) {
            context.applicationContext.dataStore.edit { prefs ->
                prefs.remove(key)
            }
        }

        /**
         * Remove Hour Data
         *
         * @param context
         * @param hour
         */
        suspend fun removeAllHourData(context: Context, hour: Int) = withContext(Dispatchers.IO) {

            removeLocationHourData(context, hour)
            removeLightHourData(context, hour)
            removeStepHourData(context, hour)
            removeScreenTimeHourData(context, hour)
            removePhoneCallData(context, hour)
        }

        /**
         * Remove Location Data
         *
         * @param context
         * @param hour
         */
        private suspend fun removeLocationHourData(context: Context, hour: Int) {

            getLocationData(context).firstOrNull()?.let { list ->

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

                context.applicationContext.dataStore.edit { it[KEY_LOCATION_DATA] = result }
            }
        }

        /**
         * Remove Light Data
         *
         * @param context
         * @param hour
         */
        private suspend fun removeLightHourData(context: Context, hour: Int) {

            getLightData(context).firstOrNull()?.let { list ->

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

                context.applicationContext.dataStore.edit { it[KEY_LIGHT_DATA] = result }
            }
        }

        /**
         * Remove Step Data
         *
         * @param context
         * @param hour
         */
        private suspend fun removeStepHourData(context: Context, hour: Int) {

            getStepData(context).firstOrNull()?.let { list ->

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

                context.applicationContext.dataStore.edit { it[KEY_STEP_DATA] = result }
            }
        }

        /**
         * Remove ScreenTime Data
         *
         * @param context
         * @param hour
         */
        private suspend fun removeScreenTimeHourData(context: Context, hour: Int) {

            getScreenTimeData(context).firstOrNull()?.let { list ->

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

                context.applicationContext.dataStore.edit { it[KEY_SCREEN_DATA] = result }
            }
        }

        /**
         * Remove Phone Call Data
         *
         * @param context
         * @param hour
         */
        private suspend fun removePhoneCallData(context: Context, hour: Int) {

            getPhoneCallData(context).firstOrNull()?.let { list ->

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

                context.applicationContext.dataStore.edit { it[KEY_PHONE_CALL_DATA] = result }
            }
        }
    }
}