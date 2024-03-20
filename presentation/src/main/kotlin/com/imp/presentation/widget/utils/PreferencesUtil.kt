package com.imp.presentation.widget.utils

import android.content.Context


class PreferencesUtil {

    companion object {

        const val PREF_NAME = "com.imp.fluffymood"

        // Member Key
        const val AUTO_LOGIN_ID_KEY = "AUTO_LOGIN_ID_KEY"
        const val AUTO_LOGIN_PASSWORD_KEY = "AUTO_LOGIN_PASSWORD_KEY"

        // Tracking Key
        const val TRACKING_SWITCH_KEY = "TRACKING_SWITCH_KEY"
        const val TRACKING_SCREEN_AWAKE_KEY = "TRACKING_SCREEN_AWAKE_KEY"

        // Set Preferences
        @JvmStatic
        fun setPreferencesString(context: Context, key: String, value: String) =
            context.getSharedPreferences(PREF_NAME, 0).edit()
                .putString(key, value)
                .commit()

        @JvmStatic
        fun setPreferencesInt(context: Context, key: String, value: Int) =
            context.getSharedPreferences(PREF_NAME, 0).edit()
                .putInt(key, value)
                .commit()

        @JvmStatic
        fun setPreferencesLong(context: Context, key: String, value: Long) =
            context.getSharedPreferences(PREF_NAME, 0).edit()
                .putLong(key, value)
                .commit()

        @JvmStatic
        fun setPreferencesBoolean(context: Context, key: String, value: Boolean) =
            context.getSharedPreferences(PREF_NAME, 0).edit()
                .putBoolean(key, value)
                .commit()

        // Get Preferences
        @JvmStatic
        fun getPreferencesString(context: Context, key: String): String =
            context.getSharedPreferences(PREF_NAME, 0).getString(key, "") ?: ""

        @JvmStatic
        fun getPreferencesInt(context: Context, key: String): Int =
            context.getSharedPreferences(PREF_NAME, 0).getInt(key, 0)

        @JvmStatic
        fun getPreferencesLong(context: Context, key: String): Long =
            context.getSharedPreferences(PREF_NAME, 0).getLong(key, 0L)

        @JvmStatic
        fun getPreferencesBoolean(context: Context, key: String): Boolean =
            context.getSharedPreferences(PREF_NAME, 0).getBoolean(key, false)


        // Delete Preferences
        @JvmStatic
        fun deletePreferences(context: Context, key: String): Boolean =
            context.getSharedPreferences(PREF_NAME, 0).edit()
                .remove(key)
                .commit()
    }
}