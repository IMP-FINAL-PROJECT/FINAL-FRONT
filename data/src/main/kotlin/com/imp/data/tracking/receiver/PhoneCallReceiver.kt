package com.imp.data.tracking.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.imp.data.tracking.data.SensorDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

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
        }
    }
}