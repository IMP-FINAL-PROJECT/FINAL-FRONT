package com.imp.presentation.widget.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.imp.presentation.R
import kotlin.math.ceil

/**
 * Rounded BarChart
 */
class RoundedHorizontalBarChart : HorizontalBarChart {

    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        readRadiusAttr(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        readRadiusAttr(context, attrs)
    }

    private fun readRadiusAttr(context: Context, attrs: AttributeSet) {

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.RoundedBarChart, 0, 0)
        try {
            setRadius(a.getDimensionPixelSize(R.styleable.RoundedBarChart_radius, 0))
        } finally {
            a.recycle()
        }

        initDisplay()
    }

    private fun initDisplay() {

        setNoDataText("")
        setNoDataTextColor(0xFFFFFF)

        setRadius(4)
        setTouchEnabled(false)
        setDrawGridBackground(false)
        getTransformer(YAxis.AxisDependency.LEFT)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        axisRight.isEnabled = false
        extraBottomOffset = 10f

        description.isEnabled = false
        legend.isEnabled = false

        xAxis.apply {

            setDrawAxisLine(false)
            setDrawGridLines(false)
            typeface = ResourcesCompat.getFont(context, R.font.suit_regular)
            textColor = ContextCompat.getColor(context, R.color.color_999999)
            textSize = 14f
            enableGridDashedLine(2f, 2f, 0f)
            labelCount = 2

            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 1f) "주간" else "야간"
                }
            }
        }

        axisLeft.apply {

            setDrawAxisLine(false)
            typeface = ResourcesCompat.getFont(context, R.font.suit_regular)
            textColor = ContextCompat.getColor(context, R.color.color_999999)
            textSize = 10f
            enableGridDashedLine(10f,10f,0f)
            setAxisLeft(2, 0f, 10f)

            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
        }
    }

    fun setAxisLeft(labelCnt: Int, yMin: Float, yMax: Float) {

        axisLeft.apply {

            labelCount = labelCnt
            axisMinimum = yMin
            axisMaximum = yMax
        }
    }

    fun setRadius(radius: Int) {
        renderer = RoundedHorizontalBarChartRenderer(this, animator, viewPortHandler, radius)
    }

    private class RoundedHorizontalBarChartRenderer(
        chart: BarDataProvider?,
        animator: ChartAnimator?,
        viewPortHandler: ViewPortHandler?,
        private val mRadius: Int
    ) :
        HorizontalBarChartRenderer(chart, animator, viewPortHandler) {

        private val mBarShadowRectBuffer = RectF()

        override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {

            val barData = mChart.barData
            for (high in indices) {

                val set = barData.getDataSetByIndex(high.dataSetIndex)
                if (set == null || !set.isHighlightEnabled) continue

                val e = set.getEntryForXValue(high.x, high.y)
                if (!isInBoundsX(e, set)) continue

                val trans = mChart.getTransformer(set.axisDependency)
                mHighlightPaint.color = set.highLightColor
                mHighlightPaint.alpha = set.highLightAlpha

                val isStack = high.stackIndex >= 0 && e.isStacked
                val y1: Float
                val y2: Float

                if (isStack) {

                    if (mChart.isHighlightFullBarEnabled) {

                        y1 = e.positiveSum
                        y2 = -e.negativeSum

                    } else {

                        val range = e.ranges[high.stackIndex]
                        y1 = range.from
                        y2 = range.to
                    }
                } else {

                    y1 = e.y
                    y2 = 0f
                }

                prepareBarHighlight(e.x, y1, y2, barData.barWidth / 2f, trans)
                setHighlightDrawPos(high, mBarRect)
                c.drawRoundRect(mBarRect, mRadius.toFloat(), mRadius.toFloat(), mHighlightPaint)
            }
        }

        override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {

            val trans = mChart.getTransformer(dataSet.axisDependency)
            mBarBorderPaint.color = dataSet.barBorderColor
            mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)

            val drawBorder = dataSet.barBorderWidth > 0f
            val phaseX = mAnimator.phaseX
            val phaseY = mAnimator.phaseY

            // draw the bar shadow before the values
            if (mChart.isDrawBarShadowEnabled) {

                mShadowPaint.color = dataSet.barShadowColor

                val barData = mChart.barData
                val barWidth = barData.barWidth
                val barWidthHalf = barWidth / 2.0f

                var x: Float
                var i = 0

                val count = ceil((dataSet.entryCount.toFloat() * phaseX).toDouble()).toInt().coerceAtMost(dataSet.entryCount)
                while (i < count) {

                    val e = dataSet.getEntryForIndex(i)
                    x = e.x

                    mBarShadowRectBuffer.left = x - barWidthHalf
                    mBarShadowRectBuffer.right = x + barWidthHalf

                    trans.rectValueToPixel(mBarShadowRectBuffer)

                    if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {

                        i++
                        continue
                    }

                    if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) break

                    mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                    mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()
                    c.drawRoundRect(mBarShadowRectBuffer, mRadius.toFloat(), mRadius.toFloat(), mShadowPaint)

                    i++
                }
            }

            // initialize the buffer
            val buffer = mBarBuffers[index]
            buffer.setPhases(phaseX, phaseY)
            buffer.setDataSet(index)
            buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
            buffer.setBarWidth(mChart.barData.barWidth)
            buffer.feed(dataSet)

            trans.pointValuesToPixel(buffer.buffer)

            val isSingleColor = dataSet.colors.size == 1
            if (isSingleColor) {
                mRenderPaint.color = dataSet.color
            }

            var j = 0
            while (j < buffer.size()) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4
                    continue
                }

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
                if (!isSingleColor) {

                    // Set the color for the currently drawn value. If the index
                    // is out of bounds, reuse colors.
                    mRenderPaint.color = dataSet.getColor(j / 4)
                }

                if (dataSet.gradientColor != null) {

                    val gradientColor = dataSet.gradientColor
                    mRenderPaint.shader = LinearGradient(
                        buffer.buffer[j],
                        buffer.buffer[j + 3],
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        gradientColor.startColor,
                        gradientColor.endColor,
                        Shader.TileMode.MIRROR
                    )
                }

                if (dataSet.gradientColors != null) {

                    mRenderPaint.shader = LinearGradient(
                        buffer.buffer[j],
                        buffer.buffer[j + 3],
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        dataSet.getGradientColor(j / 4).startColor,
                        dataSet.getGradientColor(j / 4).endColor,
                        Shader.TileMode.MIRROR
                    )
                }

                c.drawRoundRect(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRadius.toFloat(), mRadius.toFloat(), mRenderPaint
                )

                if (drawBorder) {

                    c.drawRoundRect(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRadius.toFloat(), mRadius.toFloat(), mBarBorderPaint
                    )
                }

                j += 4
            }
        }
    }
}