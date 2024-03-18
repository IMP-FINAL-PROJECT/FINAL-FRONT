package com.imp.presentation.widget.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.imp.presentation.R

/**
 * MpChart Util
 */
class ChartUtil {

    companion object {

        /**
         * BarChart Data Set
         *
         * @param context
         * @param entries
         * @return
         */
        fun barChartDataSet(
            context: Context,
            entries: ArrayList<BarEntry>
        ): BarDataSet {

            return BarDataSet(entries, "").apply {

                color = ContextCompat.getColor(context, R.color.color_3377ff)
                barBorderColor = ContextCompat.getColor(context, R.color.color_3377ff)
                label = null
                setDrawValues(false)
            }
        }

        /**
         * LineChart Data Set
         *
         * @param context
         * @param entries
         * @return
         */
        fun lineChartDataSet(
            context: Context,
            entries: ArrayList<Entry>
        ): LineDataSet {

            return LineDataSet(entries, "").apply {

                // mode = LineDataSet.Mode.CUBIC_BEZIER   // Radius 속성
                color = ContextCompat.getColor(context, R.color.color_3377ff)
                lineWidth = 2f
                label = null
                setDrawValues(false)
                setDrawCircleHole(false)
                setDrawCircles(false)
                setDrawHorizontalHighlightIndicator(false)
                setDrawHighlightIndicators(false)
            }
        }

        /**
         * Get Axis Label Count
         *
         * @param max
         * @return
         */
        fun getLabelCount(max: Float): Int {
            return if (max < 5) max.toInt() else 5
        }

        /**
         * Mapping Bar Entry Data
         */
        fun mappingBarEntryData(list: ArrayList<Float>): ArrayList<BarEntry> {

            val entries = ArrayList<BarEntry>()

            list.forEachIndexed { index, value ->
                entries.add(BarEntry(index.toFloat(), value))
            }

            return entries
        }

        /**
         * Mapping Entry Data
         */
        fun mappingEntryData(list: ArrayList<Float>): ArrayList<Entry> {

            val entries = ArrayList<Entry>()

            list.forEachIndexed { index, value ->
                entries.add(Entry(index.toFloat(), value))
            }

            return entries
        }
    }
}