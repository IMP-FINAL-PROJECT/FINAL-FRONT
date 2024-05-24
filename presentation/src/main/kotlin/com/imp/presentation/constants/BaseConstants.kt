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

        /**
         * Intent Key
         */
        const val INTENT_KEY_LOCATION = "LOCATION"
        const val INTENT_KEY_LIGHT_SENSOR = "LIGHT_SENSOR"
        const val INTENT_KEY_STEP_SENSOR = "STEP_SENSOR"

        /**
         * Analysis Type
         */
        const val ANALYSIS_TYPE_SCREEN_TIME = "ANALYSIS_TYPE_SCREEN_TIME"
        const val ANALYSIS_TYPE_SCREEN_AWAKE = "ANALYSIS_TYPE_SCREEN_AWAKE"
        const val ANALYSIS_TYPE_STEP = "ANALYSIS_TYPE_STEP"
        const val ANALYSIS_TYPE_CALL_TIME = "ANALYSIS_TYPE_CALL_TIME"
        const val ANALYSIS_TYPE_CALL_FREQUENCY = "ANALYSIS_TYPE_CALL_FREQUENCY"
        const val ANALYSIS_TYPE_LIGHT = "ANALYSIS_TYPE_LIGHT"

        /**
         * Gender Type
         */
        const val GENDER_TYPE_MALE = "M"
        const val GENDER_TYPE_FEMALE = "F"
        const val GENDER_TYPE_NONE = "N"

        /**
         * Chat Click Type
         */
        const val CHAT_CLICK_TYPE_CHATTING = "CHATTING"
        const val CHAT_CLICK_TYPE_EXIT = "EXIT"

        /**
         * Terms Type
         */
        const val TERMS_TYPE_USAGE = "USAGE"
        const val TERMS_TYPE_PRIVACY = "PRIVACY"
        const val TERMS_TYPE_SENSITIVE = "SENSITIVE"
        const val TERMS_TYPE_OPTIONAL_SENSITIVE = "OPTIONAL_SENSITIVE"
    }
}