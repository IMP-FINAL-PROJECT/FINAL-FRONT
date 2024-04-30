package com.imp.presentation.widget.component

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.imp.presentation.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar
import kotlin.math.roundToInt

/**
 * Common LineChart
 */
class CommonLineChart: LineChart {

    private val weekList: ArrayList<String> = ArrayList()

    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initDisplay()
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initDisplay()
    }

    private fun initDisplay() {

        setNoDataText("")
        setNoDataTextColor(0xFFFFFF)

        setTouchEnabled(false)
        setDrawGridBackground(false)
        getTransformer(YAxis.AxisDependency.LEFT)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        axisRight.isEnabled = false
        extraBottomOffset = 10f

        description.isEnabled = false

        xAxis.apply {

            setDrawAxisLine(false)
            setDrawGridLines(false)
            typeface = ResourcesCompat.getFont(context, R.font.suit_regular)
            textColor = ContextCompat.getColor(context, R.color.color_999999)
            textSize = 10f
            enableGridDashedLine(10f, 10f, 0f)
        }

        axisLeft.apply {

            setDrawAxisLine(false)
            typeface = ResourcesCompat.getFont(context, R.font.suit_regular)
            textColor = ContextCompat.getColor(context, R.color.color_999999)
            textSize = 10f
            enableGridDashedLine(10f,10f,0f)
        }

        legend.apply {

            isEnabled = false
            form = Legend.LegendForm.EMPTY
            typeface = ResourcesCompat.getFont(context, R.font.suit_regular)
            textColor = ContextCompat.getColor(context, R.color.color_999999)
            textSize = 10f
        }
    }

    fun setAxisLeft(labelCnt: Int, yMin: Float, yMax: Float) {

        axisLeft.apply {

            labelCount = labelCnt
            axisMinimum = yMin
            axisMaximum = yMax
        }
    }

    fun setChartWeek(isDay: Boolean, calendar: Calendar) {

        initWeekList(calendar)

        xAxis.valueFormatter =  object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {

                return if (isDay) {
                    value.toInt().toString()
                } else {
                    var pos = value.roundToInt()
                    if (pos < 0) pos = 0
                    if (pos >= weekList.size && weekList.isNotEmpty()) pos /= weekList.size
                    weekList[pos]
                }
            }
        }

        val visibleXRange = if (isDay) 24f else 7f
        setVisibleXRange(visibleXRange, visibleXRange)
    }

    private fun initWeekList(calendar: Calendar) {

        val date = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(
            Calendar.DAY_OF_MONTH))

        weekList.clear()
        weekList.add(getDayOfWeek(date, 6))
        weekList.add(getDayOfWeek(date, 5))
        weekList.add(getDayOfWeek(date, 4))
        weekList.add(getDayOfWeek(date, 3))
        weekList.add(getDayOfWeek(date, 2))
        weekList.add(getDayOfWeek(date, 1))
        weekList.add(getDayOfWeek(date, 0))
    }

    private fun getDayOfWeek(date: LocalDate, minusDay: Long): String {
        return dayOfWeekMapper(date.minusDays(minusDay).dayOfWeek)
    }

    private fun dayOfWeekMapper(dayOfWeek: DayOfWeek): String {

        if (context == null) return ""

        return when(dayOfWeek) {

            DayOfWeek.MONDAY -> context.getString(R.string.monday)
            DayOfWeek.TUESDAY -> context.getString(R.string.tuesday)
            DayOfWeek.WEDNESDAY -> context.getString(R.string.wednesday)
            DayOfWeek.THURSDAY -> context.getString(R.string.thursday)
            DayOfWeek.FRIDAY -> context.getString(R.string.friday)
            DayOfWeek.SATURDAY -> context.getString(R.string.saturday)
            DayOfWeek.SUNDAY -> context.getString(R.string.sunday)
            else -> ""
        }
    }
}