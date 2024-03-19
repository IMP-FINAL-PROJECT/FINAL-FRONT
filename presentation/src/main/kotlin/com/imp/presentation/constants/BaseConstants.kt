package com.imp.presentation.constants

class BaseConstants {

    companion object {

        /**
         * Main Navigation Label
         */
        const val MAIN_NAV_LABEL_HOME = "HOME"
        const val MAIN_NAV_LABEL_LOG = "LOG"
        const val MAIN_NAV_LABEL_CHAT = "CHAT"
        const val MAIN_NAV_LABEL_MYPAGE = "MYPAGE"

        /**
         * Intent Action Type
         */
        const val ACTION_TYPE_UPDATE_LOCATION = "ACTION_TYPE_UPDATE_LOCATION"
        const val ACTION_TYPE_UPDATE_LIGHT_SENSOR = "ACTION_TYPE_UPDATE_LIGHT_SENSOR"
        const val ACTION_TYPE_UPDATE_STEP_SENSOR = "ACTION_TYPE_UPDATE_STEP_SENSOR"
        const val ACTION_TYPE_UPDATE_CALL_STATE = "ACTION_TYPE_UPDATE_CALL_STATE"

        /**
         * Intent Key
         */
        const val INTENT_KEY_LOCATION = "LOCATION"
        const val INTENT_KEY_LIGHT_SENSOR = "LIGHT_SENSOR"
        const val INTENT_KEY_STEP_SENSOR = "STEP_SENSOR"
        const val INTENT_KEY_CALL_STATE = "CALL_STATE"

        /**
         * Screen State
         */
        const val SCREEN_STATE_ON = "ON"
        const val SCREEN_STATE_OFF = "OFF"

        /**
         * Work
         */
        const val SAVE_DATA_WORK = "SAVE_DATA_WORK"

        /**
         * Notification
         */
        const val CHANNEL_ID = "TrackingServiceChannel"
        const val CHANNEL_ID_2 = "TrackingServiceChannel2"
        const val NOTIFICATION_ID = 1
    }
}