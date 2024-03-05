package com.imp.presentation.widget.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.imp.presentation.R

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
    }
}