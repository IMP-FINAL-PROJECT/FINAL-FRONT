package com.imp.data.tracking.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.imp.data.tracking.data.SensorDataStore
import com.imp.data.tracking.util.DateUtil
import com.imp.data.util.PreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Screen State Broadcast Receiver
 */
class ScreenStateReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (context == null || intent == null) return

        when(intent.action) {

            Intent.ACTION_SCREEN_ON -> { saveScreenState(context, isOn = true) }
            Intent.ACTION_SCREEN_OFF -> { saveScreenState(context, isOn = false) }
        }
    }

    /**
     * Save Screen State
     *
     * @param context
     * @param isOn
     */
    private fun saveScreenState(context: Context, isOn: Boolean) {

        val state = if (isOn) "ON" else "OFF"

        Log.d("screen", "ScreenStateReceiver >>> Current State: $state")

        CoroutineScope(Dispatchers.IO).launch {

            // Save Screen On/Off Data into Preference DataStore
            SensorDataStore.saveScreenData(context, state, System.currentTimeMillis())

            if (!isOn) {

                // 날짜 확인
                checkDate(context)

                // screen time, awake count 갱신
                updateScreenTime(context)
                updateScreenAwakeCount(context)

            } else {

                // 초기화
                PreferencesUtil.deletePreferences(context, PreferencesUtil.TRACKING_SCREEN_RECENT_TIMESTAMP_KEY)
            }
        }
    }

    /**
     * Update Screen Time
     *
     * @param context
     */
    private fun updateScreenTime(context: Context) {

        val currentTimestamp = System.currentTimeMillis()
        var recentTimestamp = PreferencesUtil.getPreferencesLong(context, PreferencesUtil.TRACKING_SCREEN_RECENT_TIMESTAMP_KEY)
        if (recentTimestamp == 0L) {
            recentTimestamp = currentTimestamp
        }

        var screenTime = PreferencesUtil.getPreferencesLong(context, PreferencesUtil.TRACKING_SCREEN_TIME_KEY)
        screenTime += currentTimestamp - recentTimestamp

        // screen time 저장
        PreferencesUtil.setPreferencesLong(context, PreferencesUtil.TRACKING_SCREEN_TIME_KEY, screenTime)

        // recent timestamp 저장
        PreferencesUtil.setPreferencesLong(context, PreferencesUtil.TRACKING_SCREEN_RECENT_TIMESTAMP_KEY, currentTimestamp)
    }

    /**
     * Update Screen Awake Count
     *
     * @param context
     */
    private fun updateScreenAwakeCount(context: Context) {

        val count = PreferencesUtil.getPreferencesInt(context, PreferencesUtil.TRACKING_SCREEN_AWAKE_KEY)

        // screen awake count 저장
        PreferencesUtil.setPreferencesInt(context, PreferencesUtil.TRACKING_SCREEN_AWAKE_KEY, count + 1)
    }

    /**
     * Check Date
     *
     * @param context
     */
    private fun checkDate(context: Context) {

        val savedDateString = PreferencesUtil.getPreferencesString(context, PreferencesUtil.TRACKING_DATE_KEY)
        if (savedDateString.isEmpty()) {

            // 비어 있는 경우, 현재 날짜 저장 후 리턴
            PreferencesUtil.setPreferencesString(context, PreferencesUtil.TRACKING_DATE_KEY, LocalDate.now().toString())
            clearTrackingData(context)
            return
        }

        val savedDate = DateUtil.stringToLocalDate(savedDateString)
        val currentDate = LocalDate.now()

        if (currentDate.isAfter(savedDate)) {

            // 날짜 갱신
            PreferencesUtil.setPreferencesString(context, PreferencesUtil.TRACKING_DATE_KEY, currentDate.toString())

            // 초기화
            clearTrackingData(context)
        }
    }

    /**
     * Clear Tracking Data
     *
     * @param context
     */
    private fun clearTrackingData(context: Context) {

        PreferencesUtil.deletePreferences(context, PreferencesUtil.TRACKING_SCREEN_AWAKE_KEY)
        PreferencesUtil.deletePreferences(context, PreferencesUtil.TRACKING_SCREEN_TIME_KEY)
        PreferencesUtil.setPreferencesLong(context, PreferencesUtil.TRACKING_SCREEN_RECENT_TIMESTAMP_KEY, System.currentTimeMillis())
        PreferencesUtil.deletePreferences(context, PreferencesUtil.TRACKING_STEP_KEY)
    }
}