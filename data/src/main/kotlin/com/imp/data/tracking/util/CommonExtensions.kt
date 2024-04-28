package com.imp.data.tracking.util

import java.util.Calendar

/**
 * Calendar
 */
fun Calendar.resetCalendarTime() {

    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

fun Calendar.resetCalendarSecond() {

    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}