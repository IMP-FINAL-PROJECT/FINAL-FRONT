package com.imp.presentation.widget.extension

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.imp.presentation.widget.utils.CommonUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.Calendar

/**
 * Intent
 */
inline fun <reified T : Parcelable> Intent.getCompatibleParcelableExtra(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(key)
    }
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}


/**
 * Bundle
 */
inline fun <reified T : Parcelable> Bundle.getCompatibleParcelable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key)
    }
}

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

/**
 * Visibility
 */
fun Boolean.toGoneOrVisible(): Int {
    return if (this) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun Boolean.toVisibleOrGone(): Int {
    return if (this) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun Boolean.toVisibleOrInvisible(): Int {
    return if (this) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

fun Boolean.toInVisibleOrVisible(): Int {
    return if (this) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}

/**
 * View
 */
fun View.ripple(): View {

    val value = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, value, true)
    setBackgroundResource(value.resourceId)
    isFocusable = true
    return this
}

/**
 * ViewPager (f와 민감도는 반비례)
 */
fun ViewPager2.setDragSensitivity(f: Int = 0) {

    val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
    recyclerViewField.isAccessible = true
    val recyclerView = recyclerViewField.get(this) as RecyclerView

    val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
    touchSlopField.isAccessible = true
    val touchSlop = touchSlopField.get(recyclerView) as Int
    touchSlopField.set(recyclerView, touchSlop*f)
}

/**
 * RecyclerView (f와 민감도는 반비례)
 */
fun RecyclerView.setDragSensitivity(f: Int = 1) {

    val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
    touchSlopField.isAccessible = true
    val touchSlop = touchSlopField.getInt(this)
    touchSlopField.setInt(this, touchSlop * f)
}

/**
 * 키보드 올리기
 */
fun EditText.focusAndShowKeyboard(context: Context?) {

    CoroutineScope(Dispatchers.Main).launch {
        delay(50)

        requestFocus()

        if ( context == null ) {
            CommonUtil.log("focusAndShowKeyboard context is null")
            return@launch
        }

        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this@focusAndShowKeyboard, InputMethodManager.SHOW_IMPLICIT)
    }
}

/**
 * 키보드 내리기
 */
fun hideKeyboard(context: Context?, view: View?, vararg editText: AppCompatEditText?) {

    editText.forEach { it?.clearFocus() }

    if ( context == null ) {
        CommonUtil.log("hideKeyboard context is null")
        return
    }

    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
}

/**
 * Calendar
 */
fun Calendar.resetCalendarTime() {

    set(Calendar.HOUR, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}