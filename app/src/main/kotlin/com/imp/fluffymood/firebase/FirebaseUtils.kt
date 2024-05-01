package com.imp.fluffymood.firebase

import android.annotation.SuppressLint
import android.content.Context
import android.os.PowerManager
import com.google.firebase.messaging.FirebaseMessaging
import com.imp.presentation.widget.utils.CommonUtil
import com.imp.presentation.widget.utils.PreferencesUtil

class FirebaseUtils {

    companion object {

        private var mWakeLock: PowerManager.WakeLock? = null

        /**
         * Initialize Firebase Token
         */
        fun initFirebaseToken(context: Context) {

            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    // Get new FCM registration token
                    val token = task.result?: ""
                    CommonUtil.log("Token init : $token")

                    PreferencesUtil.setPreferencesString(context, PreferencesUtil.PUSH_TOKEN, token)

                } else {

                    CommonUtil.log("Token init Fail : ${task.exception?.message}")
                }
            }
        }

        /**
         * Acquire Wake Lock
         *
         * @param context
         */
        @SuppressLint("InvalidWakeLockTag")
        fun acquireWakeLock(context: Context) {

            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            mWakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE, "WAKEUP"
            )
            mWakeLock!!.acquire(3000)
        }

        fun releaseWakeLock() {

            mWakeLock?.release()
            mWakeLock = null
        }
    }
}