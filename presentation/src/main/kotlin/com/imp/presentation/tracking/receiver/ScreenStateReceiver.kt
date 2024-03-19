package com.imp.presentation.tracking.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.imp.presentation.tracking.data.SensorDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Screen State Broadcast Receiver
 */
class ScreenStateReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent == null) return

        when(intent.action) {

            Intent.ACTION_SCREEN_ON -> { saveScreenState(isOn = true) }
            Intent.ACTION_SCREEN_OFF -> { saveScreenState(isOn = false) }
        }
    }

    /**
     * Save Screen State
     *
     * @param isOn
     */
    private fun saveScreenState(isOn: Boolean) {

        val state = if (isOn) "ON" else "OFF"

        Log.d("screen", "ScreenStateReceiver >>> Current State: $state")

        CoroutineScope(Dispatchers.IO).launch {

            // Save Screen On/Off Data into Preference DataStore
            SensorDataStore.saveScreenData(state, System.currentTimeMillis())
        }
    }
}