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

        initWeekList()

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

    fun setChartWeek(isDay: Boolean) {

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

    private fun initWeekList() {

        context?.let { ctx ->

            weekList.clear()
            weekList.add(ctx.getString(R.string.sunday))
            weekList.add(ctx.getString(R.string.monday))
            weekList.add(ctx.getString(R.string.tuesday))
            weekList.add(ctx.getString(R.string.wednesday))
            weekList.add(ctx.getString(R.string.thursday))
            weekList.add(ctx.getString(R.string.friday))
            weekList.add(ctx.getString(R.string.saturday))
        }
    }
}