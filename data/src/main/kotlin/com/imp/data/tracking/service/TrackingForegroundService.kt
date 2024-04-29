package com.imp.data.tracking.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.imp.data.tracking.constants.BaseConstants
import com.imp.data.tracking.data.SensorDataStore
import com.imp.data.tracking.receiver.PhoneCallReceiver
import com.imp.data.tracking.receiver.ScreenStateReceiver
import com.imp.data.tracking.util.resetCalendarTime
import com.imp.data.tracking.work.TrackingWorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Tracking Foreground Service
 */
class TrackingForegroundService : Service() {

    companion object {

        private const val TIME_DELAY = 5000L
        private const val TIME_INTERVAL = 5000L
    }

    /** Coroutine Scope */
    private var locationCoroutine = CoroutineScope(Dispatchers.IO)
    private var lightCoroutine = CoroutineScope(Dispatchers.IO)
    private var stepCoroutine = CoroutineScope(Dispatchers.IO)

    /** Screen State Broadcast Receiver */
    private val screenStateReceiver: ScreenStateReceiver = ScreenStateReceiver()

    /** Phone Call Broadcast Receiver */
    private val phoneCallReceiver: PhoneCallReceiver = PhoneCallReceiver()

    /** Location Manager */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationCallback: CurrentLocationCallback = CurrentLocationCallback()

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
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {

        stopForeground(STOP_FOREGROUND_REMOVE)

        locationCoroutine.cancel()
        lightCoroutine.cancel()
        stepCoroutine.cancel()

        // Unregister Broadcast Receiver
        unregisterReceiver()

        // Stop Location Update
        stopLocationUpdate()

        // Stop Sensor Update
        stopSensorUpdate()

        // Cancel Save Data Work
        cancelSaveDataWork()

        super.onDestroy()
    }

    /**
     * Initialize Foreground Notification
     */
    private fun initNotification() {

        val notificationIntent = Intent()
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = createNotificationChannel(BaseConstants.CHANNEL_ID, BaseConstants.CHANNEL_ID_2)
        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Notification.Builder(this, channelId)
        } else {
            Notification.Builder(this).setPriority(Notification.PRIORITY_DEFAULT)
        }


        builder.apply {

            setContentTitle("Phone Tracking")
//            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentIntent(pendingIntent)
            setNumber(0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                setFlag(Notification.FLAG_NO_CLEAR, true)
                setFlag(Notification.FLAG_ONGOING_EVENT, true)
                setFlag(Notification.FLAG_FOREGROUND_SERVICE, true)
            }
        }

        val foregroundNotification = builder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

            startForeground(
                BaseConstants.NOTIFICATION_ID,
                foregroundNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {

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
        nextHour.resetCalendarTime()

        val millisecondsNextHour = nextHour.timeInMillis - now.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresDeviceIdle(false)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .build()

        val workRequest = PeriodicWorkRequest.Builder(TrackingWorkManager::class.java, 1, TimeUnit.HOURS)
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
        registerPhoneCallReceiver()
    }

    /**
     * Unregister Broadcast Receiver
     */
    private fun unregisterReceiver() {

        unregisterScreenReceiver()
        unregisterPhoneCallReceiver()
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
     * Register Phone Call Broadcast Receiver
     */
    private fun registerPhoneCallReceiver() {

        IntentFilter().apply {
            addAction("android.intent.action.NEW_OUTGOING_CALL")
            addAction("android.intent.action.PHONE_STATE")
            registerReceiver(phoneCallReceiver, this, RECEIVER_EXPORTED)
        }
    }

    /**
     * Unregister Screen State Broadcast Receiver
     */
    private fun unregisterScreenReceiver() {

        try {
            unregisterReceiver(screenStateReceiver)
        } catch (e: IllegalArgumentException) {
            Log.d("tracking", "unregisterScreenReceiver >>> $e")
        }
    }

    /**
     * Unregister Phone Call Broadcast Receiver
     */
    private fun unregisterPhoneCallReceiver() {

        try {
            unregisterReceiver(phoneCallReceiver)
        } catch (e: IllegalArgumentException) {
            Log.d("tracking", "unregisterScreenReceiver >>> $e")
        }
    }

    /**
     * Set Location Listener
     */
    private fun setLocationListener() {

        if (::fusedLocationProviderClient.isInitialized.not()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        val locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(TIME_INTERVAL)
            .setFastestInterval(TIME_INTERVAL)

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    /**
     * Stop Location Update
     */
    private fun stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Set Light Sensor Listener
     */
    private fun setLightSensorListener() {

        // initialize timestamp
        currentTime = System.currentTimeMillis()

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
     * Current Location Callback
     */
    private var lastLocation: Location? = null
    inner class CurrentLocationCallback: LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            val location = locationResult.lastLocation
            if (location != null) {

                if (location.latitude == 0.0 || location.longitude == 0.0) return

                CoroutineScope(Dispatchers.IO).launch {

                    val distance = lastLocation?.distanceTo(location) ?: 0f
                    if (lastLocation == null || distance >= 20f) {

                        Log.d("location", "latitude: ${location.latitude}, longitude: ${location.longitude}, ${System.currentTimeMillis()}")

                        lastLocation = location

                        // Save Location Data into Preference DataStore
                        SensorDataStore.saveLocationData(this@TrackingForegroundService, location.latitude, location.longitude, System.currentTimeMillis())

                        // Update Location Data
                        updateLocation(location)
                    }
                }
            }
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

                            // Save Light Data into Preference DataStore
                            SensorDataStore.saveLightData(this@TrackingForegroundService, light, System.currentTimeMillis())
                        }
                        lightCoroutine.cancel()
                    }

                    // Update Light Sensor Data
                    updateLightSensor(light)
                }

                Sensor.TYPE_STEP_COUNTER -> {

                    currentStep ++

                    stepCoroutine.cancel()
                    stepCoroutine = CoroutineScope(Dispatchers.IO)
                    stepCoroutine.launch {

                        // Save Step Data into Preference DataStore
                        SensorDataStore.saveStepData(this@TrackingForegroundService, 1, System.currentTimeMillis())
                    }

                    // Update Step Sensor Data
                    updateStepSensor(currentStep)
                }
            }
        }
    }
}