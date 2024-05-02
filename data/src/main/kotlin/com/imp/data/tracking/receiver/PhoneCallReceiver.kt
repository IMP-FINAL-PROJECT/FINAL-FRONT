package com.imp.data.tracking.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.imp.data.tracking.data.SensorDataStore
import com.imp.data.tracking.util.DateUtil
import com.imp.data.util.PreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Phone Call Broadcast Receiver
 */
class PhoneCallReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (context == null || intent == null) return

        if (intent.action == "android.intent.action.PHONE_STATE") {

            val bundle = intent.extras
            val state = bundle?.getString(TelephonyManager.EXTRA_STATE)

            when(state) {

                TelephonyManager.EXTRA_STATE_RINGING -> {

                    // 통화벨 울리는 중
                    Log.d("call", "state: 통화벨 울리는 중")
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {

                    // 통화 중
                    savePhoneCallState(context, true, System.currentTimeMillis())
                    Log.d("call", "state: 통화 중")
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {

                    // 통화 종료 또는 통화벨 울리기 종료
                    CoroutineScope(Dispatchers.IO).launch {

                        val currentCallState = SensorDataStore.getCurrentCallState(context).firstOrNull()
                        if (currentCallState == true) {

                            // 현재 상태가 통화 중일 때만 저장
                            savePhoneCallState(context, false, System.currentTimeMillis())
                            Log.d("call", "state: 통화 종료")

                        } else {

                            Log.d("call", "state: 통화벨 울리기 종료")
                        }
                    }
                }
            }
        }
    }

    /**
     * Save Phone Call State
     *
     * @param context
     * @param isOn
     * @param timestamp
     */
    private fun savePhoneCallState(context: Context, isOn: Boolean, timestamp: Long) {

        val state = if (isOn) "ON" else "OFF"

        Log.d("call", "PhoneCallReceiver >>> Current State: $state")

        CoroutineScope(Dispatchers.IO).launch {

            // Save Phone Call On/Off Data into Preference DataStore
            SensorDataStore.savePhoneCallData(context, state, timestamp)

            // Save Current Phone Call On/Off Data into Preference DataStore
            SensorDataStore.saveCurrentCallState(context, isOn)

            // 날짜 확인
            checkDate(context)

            if (!isOn) {

                // call time 갱신
                updateCallTime(context)

            } else {

                // call count 갱신
                updateCallCount(context)

                // 초기화
                PreferencesUtil.deletePreferences(context, PreferencesUtil.TRACKING_CALL_RECENT_TIMESTAMP_KEY)
            }
        }
    }

    /**
     * Update Call Time
     *
     * @param context
     */
    private fun updateCallTime(context: Context) {

        val currentTimestamp = System.currentTimeMillis()
        var recentTimestamp = PreferencesUtil.getPreferencesLong(context, PreferencesUtil.TRACKING_CALL_RECENT_TIMESTAMP_KEY)
        if (recentTimestamp == 0L) {
            recentTimestamp = currentTimestamp
        }

        var callTime = PreferencesUtil.getPreferencesLong(context, PreferencesUtil.TRACKING_CALL_TIME_KEY)
        callTime += currentTimestamp - recentTimestamp

        // call time 저장
        PreferencesUtil.setPreferencesLong(context, PreferencesUtil.TRACKING_CALL_TIME_KEY, callTime)

        // recent timestamp 저장
        PreferencesUtil.deletePreferences(context, PreferencesUtil.TRACKING_CALL_RECENT_TIMESTAMP_KEY)
    }

    /**
     * Update Call Count
     *
     * @param context
     */
    private fun updateCallCount(context: Context) {

        val count = PreferencesUtil.getPreferencesInt(context, PreferencesUtil.TRACKING_CALL_COUNT_KEY)

        // screen awake count 저장
        PreferencesUtil.setPreferencesInt(context, PreferencesUtil.TRACKING_CALL_COUNT_KEY, count + 1)
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
        PreferencesUtil.deletePreferences(context, PreferencesUtil.TRACKING_CALL_COUNT_KEY)
        PreferencesUtil.deletePreferences(context, PreferencesUtil.TRACKING_CALL_TIME_KEY)
        PreferencesUtil.deletePreferences(context, PreferencesUtil.TRACKING_CALL_RECENT_TIMESTAMP_KEY)
        PreferencesUtil.deletePreferences(context, PreferencesUtil.TRACKING_STEP_KEY)
    }
}