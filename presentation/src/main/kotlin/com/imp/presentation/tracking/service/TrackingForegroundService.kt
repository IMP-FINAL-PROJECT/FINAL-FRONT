package com.imp.presentation.tracking.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.telecom.InCallService
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.imp.presentation.R
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.tracking.data.SensorDataStore
import com.imp.presentation.tracking.receiver.ScreenStateReceiver
import com.imp.presentation.tracking.work.TrackingWorkManager
import com.imp.presentation.widget.utils.CommonUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Tracking Foreground Service
 */
class TrackingForegroundService : InCallService() {

    companion object {

        private const val TIME_DELAY = 20000L
    }

    /** Coroutine Scope */
    private var locationCoroutine = CoroutineScope(Dispatchers.IO)
    private var lightCoroutine = CoroutineScope(Dispatchers.IO)
    private var stepCoroutine = CoroutineScope(Dispatchers.IO)

    /** Screen State Broadcast Receiver */
    private val screenStateReceiver: ScreenStateReceiver = ScreenStateReceiver()

    /** Location Manager */
    private lateinit var locationManager: LocationManager
    private val locationListener: CurrentLocationListener = CurrentLocationListener()

    /** Sensor Manager */
    private lateinit var sensorManager: SensorManager
    private val sensorListener: CurrentSensorListener = CurrentSensorListener()

    /** Current Step */
    private var currentStep = 0

    private var currentTime = 0L

    override fun onCreate() {
        super.onCreate()

        // Initialize Foreground Notification
        initNotification()

        // Register Broadcast Receiver
        registerReceiver()

        // Set Location Listener
        setLocationListener()

        // Set Light Sensor Listener
        setLightSensorListener()

        // Set Step Sensor Listener
        setStepSensorListener()

        // Register WorkManager
        scheduleSaveDataWork()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {

        locationCoroutine.cancel()
        lightCoroutine.cancel()
        stepCoroutine.cancel()

        // Unregister Broadcast Receiver
        unregisterReceiver()

        // Stop Location Update
        stopLocationUpdate()

        // Stop Sensor Update
        stopSensorUpdate()

        super.onDestroy()
    }

    /**
     * Initialize Foreground Notification
     */
    private fun initNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            val notificationIntent = Intent()
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val channelId = createNotificationChannel(BaseConstants.CHANNEL_ID, BaseConstants.CHANNEL_ID_2)
            val builder: Notification.Builder = Notification.Builder(this, channelId).apply {

                setContentTitle(getString(R.string.app_name))
                setSmallIcon(R.drawable.icon_mood_good)
                setContentIntent(pendingIntent)
                setNumber(0)
                setFlag(Notification.FLAG_NO_CLEAR, false)
                setFlag(Notification.FLAG_ONGOING_EVENT, true)
                setFlag(Notification.FLAG_FOREGROUND_SERVICE, true)
            }

            val foregroundNotification = builder.build()
            startForeground(BaseConstants.NOTIFICATION_ID, foregroundNotification)
        }
    }

    /**
     * Create Notification Channel
     *
     * @param channelId
     * @param channelName
     */
    private fun createNotificationChannel(channelId: String, channelName: String): String {

        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {

            setShowBadge(false)
            lightColor = Color.BLACK
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    /**
     * Schedule Save Data Work
     */
    private fun scheduleSaveDataWork() {

        val now = Calendar.getInstance()

        val nextHour = now.clone() as Calendar
        nextHour.add(Calendar.HOUR_OF_DAY, 1)
        nextHour.set(Calendar.MINUTE, 0)
        nextHour.set(Calendar.SECOND, 0)
        nextHour.set(Calendar.MILLISECOND, 0)

        val millisecondsNextHour = nextHour.timeInMillis - now.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequest.Builder(TrackingWorkManager::class.java, 15, TimeUnit.MINUTES)
            .setInitialDelay(millisecondsNextHour, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                BaseConstants.SAVE_DATA_WORK,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
    }

    /**
     * Cancel Save Data Work
     */
    private fun cancelSaveDataWork() {

        WorkManager.getInstance(this).cancelUniqueWork(BaseConstants.SAVE_DATA_WORK)
    }

    /**
     * Register Broadcast Receiver
     */
    private fun registerReceiver() {

        registerScreenReceiver()
    }

    /**
     * Unregister Broadcast Receiver
     */
    private fun unregisterReceiver() {

        unregisterScreenReceiver()
    }

    /**
     * Register Screen State Broadcast Receiver
     */
    private fun registerScreenReceiver() {

        IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            registerReceiver(screenStateReceiver, this, RECEIVER_EXPORTED)
        }
    }

    /**
     * Unregister Screen State Broadcast Receiver
     */
    private fun unregisterScreenReceiver() {

        try {
            unregisterReceiver(screenStateReceiver)
        } catch (e: IllegalArgumentException) {
            CommonUtil.log("unregisterScreenReceiver >>> $e")
        }
    }

    /**
     * Set Location Listener
     */
    private fun setLocationListener() {

        if (::locationManager.isInitialized.not()) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        val minTime: Long = TIME_DELAY  // 업데이트 시간
        val minDistance = 20f           // 업데이트 거리 (m)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            minTime, minDistance, locationListener
        )

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            minTime, minDistance, locationListener
        )
    }

    /**
     * Stop Location Update
     */
    private fun stopLocationUpdate() {
        locationManager.removeUpdates(locationListener)
    }

    /**
     * Set Light Sensor Listener
     */
    private fun setLightSensorListener() {

        if (::sensorManager.isInitialized.not()) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }

        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorManager.registerListener(
            sensorListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    /**
     * Set Step Sensor Listener
     */
    private fun setStepSensorListener() {

        // initialize step count
        currentStep = 0

        if (::sensorManager.isInitialized.not()) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }

        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(
            sensorListener,
            stepSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    /**
     * Stop Sensor Update
     */
    private fun stopSensorUpdate() {
        sensorManager.unregisterListener(sensorListener)
    }

    /**
     * Update Location Data
     *
     * @param location
     */
    private fun updateLocation(location: Location) {

        Intent(BaseConstants.ACTION_TYPE_UPDATE_LOCATION).apply {

            putExtra(BaseConstants.INTENT_KEY_LOCATION, location)
            sendBroadcast(this)
        }
    }

    /**
     * Update Light Sensor Data
     *
     * @param light
     */
    private fun updateLightSensor(light: Float) {

        Intent(BaseConstants.ACTION_TYPE_UPDATE_LIGHT_SENSOR).apply {

            putExtra(BaseConstants.INTENT_KEY_LIGHT_SENSOR, light)
            sendBroadcast(this)
        }
    }

    /**
     * Update Step Sensor Data
     *
     * @param step
     */
    private fun updateStepSensor(step: Int) {

        Intent(BaseConstants.ACTION_TYPE_UPDATE_STEP_SENSOR).apply {

            putExtra(BaseConstants.INTENT_KEY_STEP_SENSOR, step)
            sendBroadcast(this)
        }
    }

    /**
     * Update Call State
     *
     * @param state
     */
    private fun updateCallState(state: String) {

        Intent(BaseConstants.ACTION_TYPE_UPDATE_CALL_STATE).apply {

            putExtra(BaseConstants.INTENT_KEY_CALL_STATE, state)
            sendBroadcast(this)
        }
    }

    /**
     * Current Location Listener
     */
    inner class CurrentLocationListener: LocationListener {

        override fun onLocationChanged(location: Location) {

            locationCoroutine.cancel()
            locationCoroutine = CoroutineScope(Dispatchers.IO)
            locationCoroutine.launch {

                Log.d("location", "latitude: ${location.latitude}, longitude: ${location.longitude}, ${System.currentTimeMillis()}")

                // Save Location Data into Preference DataStore
                SensorDataStore.saveLocationData(location.latitude, location.longitude, System.currentTimeMillis())
            }

            // Update Location Data
            updateLocation(location)
        }
    }

    /**
     * Current Sensor Listener
     */
    inner class CurrentSensorListener: SensorEventListener {

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        override fun onSensorChanged(event: SensorEvent?) {

            if (event == null) return

            when(event.sensor.type) {

                Sensor.TYPE_LIGHT -> {

                    val light = event.values[0]

                    lightCoroutine.cancel()
                    lightCoroutine = CoroutineScope(Dispatchers.IO)
                    lightCoroutine.launch {

                        if (System.currentTimeMillis() - currentTime >= TIME_DELAY) {

                            currentTime = System.currentTimeMillis()

                            Log.d("light", "light: $light, ${System.currentTimeMillis()}")

                            // Save Light Data into Preference DataStore
                            SensorDataStore.saveLightData(light, System.currentTimeMillis())
                        }
                    }

                    // Update Light Sensor Data
                    updateLightSensor(light)
                }

                Sensor.TYPE_STEP_COUNTER -> {

                    currentStep ++

                    stepCoroutine.cancel()
                    stepCoroutine = CoroutineScope(Dispatchers.IO)
                    stepCoroutine.launch {

                        Log.d("step", "${System.currentTimeMillis()}")

                        // Save Step Data into Preference DataStore
                        SensorDataStore.saveStepData(1, System.currentTimeMillis())
                    }

                    // Update Step Sensor Data
                    updateStepSensor(currentStep)
                }
            }
        }
    }
}