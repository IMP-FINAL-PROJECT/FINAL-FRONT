package com.imp.presentation.widget.component

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.imp.presentation.R

/**
 * Common LineChart
 */
class CommonLineChart: LineChart {

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
}