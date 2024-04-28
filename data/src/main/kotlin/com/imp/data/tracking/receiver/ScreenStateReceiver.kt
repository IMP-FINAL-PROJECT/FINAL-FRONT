package com.imp.data.tracking.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.imp.data.tracking.data.SensorDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        }
    }
}