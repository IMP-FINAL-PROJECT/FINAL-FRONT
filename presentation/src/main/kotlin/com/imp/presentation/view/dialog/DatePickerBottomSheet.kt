package com.imp.presentation.view.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.imp.presentation.R
import com.imp.presentation.base.BaseBottomSheetFragment
import com.imp.presentation.databinding.SheetDatePickerBinding
import com.shawnlin.numberpicker.NumberPicker
import java.util.Calendar

/**
 * Detail Disclaimer BotthonSheet
 */
class DatePickerBottomSheet(private val calendar: Calendar, private val callback: (Calendar) -> Unit, private val dismissCallback: () -> Unit) : BaseBottomSheetFragment<SheetDatePickerBinding>() {

    /** Calendar */
    private val todayCalendar: Calendar = Calendar.getInstance()
    private val newCalendar: Calendar = Calendar.getInstance()

    /** Date Picker ValueChangeListener */
    private val valueChangedListener = NumberPicker.OnValueChangeListener { picker, _, newVal ->

        with(mBinding) {

            when (picker) {

                npYear -> {

                    newCalendar.set(Calendar.YEAR, newVal)

                    updatePicker(newCalendar)
                    controlSelectButton(newCalendar)
                }

                npMonth -> {

                    newCalendar.set(Calendar.MONTH, newVal - 1)

                    updatePicker(newCalendar)
                    controlSelectButton(newCalendar)
                }

                npDay -> {

                    newCalendar.set(Calendar.DAY_OF_MONTH, newVal)

                    controlSelectButton(newCalendar)
                }
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = SheetDatePickerBinding.inflate(inflater, container, false)

    override fun initData() {

        isFullScreen = false
        setStyle(STYLE_NORMAL, R.style.BottomModalDialogTheme)

        // set calendar
        newCalendar.time = calendar.time
    }

    override fun initView() {

        initDisplay()
        initNumberPicker()
        setOnClickListener()
    }

    override fun onDestroyView() {

        dismissCallback.invoke()
        super.onDestroyView()
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // title
            tvTitle.text = getString(R.string.log_text_11)

            // select
            incSelect.tvButton.text = getString(R.string.select)
            controlSelectButton(newCalendar)
        }
    }

    /**
     * Initialize NumberPicker
     */
    private fun initNumberPicker() {

        context?.let { ctx ->

            with(mBinding) {

                val npFont = ResourcesCompat.getFont(ctx, R.font.suit_light)
                val npSelectedFont = ResourcesCompat.getFont(ctx, R.font.suit_medium)

                npYear.typeface = npFont
                npYear.setSelectedTypeface(npSelectedFont)

                npMonth.typeface = npFont
                npMonth.setSelectedTypeface(npSelectedFont)

                npDay.typeface = npFont
                npDay.setSelectedTypeface(npSelectedFont)

                npYear.maxValue = todayCalendar.get(Calendar.YEAR)
                npYear.value = calendar.get(Calendar.YEAR)
                npYear.setOnValueChangedListener(valueChangedListener)
                npYear.wrapSelectorWheel = false

                val monthFormat = Array(12) { String.format("%02d", it + 1) }
                npMonth.value = calendar.get(Calendar.MONTH) + 1
                npMonth.displayedValues = monthFormat
                npMonth.setOnValueChangedListener(valueChangedListener)
                npMonth.wrapSelectorWheel = false

                val dayFormat = Array(npDay.maxValue) { String.format("%02d", it + 1) }
                npDay.maxValue = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                npDay.value = calendar.get(Calendar.DAY_OF_MONTH)
                npDay.displayedValues = dayFormat
                npDay.setOnValueChangedListener(valueChangedListener)
                npDay.wrapSelectorWheel = false
            }
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 닫기
            ivClose.setOnClickListener { dismiss() }

            // 날짜 선택
            incSelect.tvButton.setOnClickListener {

                newCalendar.set(Calendar.DAY_OF_MONTH, npDay.value)

                callback.invoke(newCalendar)
                dismiss()
            }
        }
    }

    /**
     * Update Date Picker
     *
     * @param newCalendar
     */
    private fun updatePicker(newCalendar: Calendar) {

        with(mBinding) {

            npDay.maxValue = newCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
    }

    /**
     * Control Selected Button
     *
     * @param newCalendar
     */
    private fun controlSelectButton(newCalendar: Calendar) {

        with(mBinding) {

            incSelect.tvButton.isEnabled = calendar.time != newCalendar.time && todayCalendar.time >= newCalendar.time
        }
    }
}