package com.imp.presentation.widget.extension

import android.content.Context
import android.util.TypedValue
import com.imp.presentation.widget.utils.DateUtil
import java.text.DecimalFormat


fun Int.toDp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
)

fun Float.toDp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics
)

fun Int.thousandUnits(): String {
    val decimal = DecimalFormat("#,###")
    return decimal.format(this)
}

/**
 * Float ArrayList 변환
 */
fun ArrayList<Int>.toFloatArray(): ArrayList<Float> {

    val list = ArrayList<Float>()
    this.forEach { list.add(it.toFloat()) }

    return list
}

/**
 * Set Chart Max List
 */
fun ArrayList<Float>.setMaxSize(isDay: Boolean): ArrayList<Float> {

    if (isDay) {

        if (size > 24) return this
        for (i in 0 until (24 - size)) { add(0f) }

    } else {

        if (size > 7) return this
        for (i in 0 until (7 - size)) { add(0f) }
    }

    return this
}

/**
 * Change to Minute List
 */
fun ArrayList<Int>.toMinuteList(): ArrayList<Int> {

    val list = ArrayList<Int>()
    forEach {
        list.add(DateUtil.getMin(it.toLong()))
    }

    return list
}