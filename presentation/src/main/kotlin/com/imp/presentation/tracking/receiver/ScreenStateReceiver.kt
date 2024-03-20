package com.imp.presentation.tracking.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.imp.presentation.tracking.data.SensorDataStore
import com.imp.presentation.widget.utils.PreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Screen State Broadcast Receiver
 */
class ScreenStateReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent == null || context == null) return

        when(intent.action) {

            Intent.ACTION_SCREEN_ON -> { saveScreenState(context, isOn = true) }
            Intent.ACTION_SCREEN_OFF -> { saveScreenState(context, isOn = false) }
        }
    }

    /**
     * Save Screen State
     *
     * @param isOn
     */
    private fun saveScreenState(context: Context, isOn: Boolean) {

        val state = if (isOn) "ON" else "OFF"

        Log.d("screen", "ScreenStateReceiver >>> Current State: $state")

        CoroutineScope(Dispatchers.IO).launch {

            // Save Screen On/Off Data into Preference DataStore
            SensorDataStore.saveScreenData(state, System.currentTimeMillis())
        }

        // todo: 임시
        var count = PreferencesUtil.getPreferencesInt(context, PreferencesUtil.TRACKING_SCREEN_AWAKE_KEY)
        if (isOn) count++

        PreferencesUtil.setPreferencesInt(context, PreferencesUtil.TRACKING_SCREEN_AWAKE_KEY, count)
    }
}