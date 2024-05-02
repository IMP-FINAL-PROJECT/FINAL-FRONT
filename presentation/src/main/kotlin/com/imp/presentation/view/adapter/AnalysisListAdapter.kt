package com.imp.presentation.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.imp.domain.model.AnalysisModel
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.ItemAnalysisBinding
import com.imp.presentation.widget.utils.ChartUtil
import com.imp.presentation.widget.utils.DateUtil
import kotlin.math.ceil

/**
 * Main - Analysis List Adapter
 */
class AnalysisListAdapter(var context: Context?, val list: ArrayList<Pair<String, String>>, private var analysisData: AnalysisModel) : RecyclerView.Adapter<AnalysisListAdapter.CustomViewHolder>() {

    interface SelectItem { fun selectItem(position: Int, type: String) }
    var selectItem: SelectItem? = null

    /**
     * Set Analysis Data
     *
     * @param dao
     */
    fun setAnalysisData(dao: AnalysisModel) {

        analysisData = dao
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        val binding = ItemAnalysisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        if (position < list.size) {
            holder.bindView(context, position, list[position], analysisData, selectItem, list.size)
        }
    }

    class CustomViewHolder(private val binding: ItemAnalysisBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(context: Context?, position: Int, dao: Pair<String, String>, analysisData: AnalysisModel, selectItem: SelectItem?, size: Int) {

            context?.let { ctx ->

                with(binding) {

                    // title
                    tvTitle.text = dao.second

                    val dataList = getCharDataList(dao.first, analysisData)
                    val barDataSet = ChartUtil.barChartDataSet(ctx, dataList)

                    chart.data = BarData(barDataSet).apply { barWidth = 0.5f }
                    chart.invalidate()
                }
            }
        }

        /**
         * Get Chart DataList
         *
         * @param type
         * @param dao
         * @return ArrayList<BarEntry>
         */
        private fun getCharDataList(type: String, dao: AnalysisModel): ArrayList<BarEntry> {

            with(binding) {

                val dataList = ArrayList<BarEntry>()
                when(type) {

                    BaseConstants.ANALYSIS_TYPE_SCREEN_TIME -> {

                        val day = DateUtil.timestampToScreenTime(dao.day_phone_use_duration.toLong())
                        val night = DateUtil.timestampToScreenTime(dao.night_phone_use_duration.toLong())

                        dataList.add(BarEntry(1f, "${day.first}.${day.second}".toFloat()))
                        dataList.add(BarEntry(0f, "${night.first}.${night.second}".toFloat()))

                        chart.setAxisLeft(4, 0f, 12f)
                    }
                    BaseConstants.ANALYSIS_TYPE_SCREEN_AWAKE -> {

                        dataList.add(BarEntry(1f, dao.day_phone_use_frequency.toFloat()))
                        dataList.add(BarEntry(0f, dao.night_phone_use_frequency.toFloat()))

                        val maxValue = getAxisLeftMax(dao.day_phone_use_frequency, dao.night_phone_use_frequency)
                        chart.setAxisLeft(ChartUtil.getLabelCount(maxValue.first), 0f, maxValue.second)
                    }
                    BaseConstants.ANALYSIS_TYPE_STEP -> {

                        dataList.add(BarEntry(1f, dao.day_step_count.toFloat()))
                        dataList.add(BarEntry(0f, dao.night_step_count.toFloat()))

                        val maxValue = getAxisLeftMax(dao.day_step_count, dao.night_step_count)
                        chart.setAxisLeft(ChartUtil.getLabelCount(maxValue.first), 0f, maxValue.second)
                    }
                    BaseConstants.ANALYSIS_TYPE_CALL_TIME -> {

                        val day = DateUtil.timestampToScreenTime(dao.day_call_use_duration.toLong())
                        val night = DateUtil.timestampToScreenTime(dao.night_call_use_duration.toLong())

                        dataList.add(BarEntry(1f, "${day.first}.${day.second}".toFloat()))
                        dataList.add(BarEntry(0f, "${night.first}.${night.second}".toFloat()))

                        chart.setAxisLeft(6, 0f, 12f)
                    }
                    BaseConstants.ANALYSIS_TYPE_CALL_FREQUENCY -> {

                        dataList.add(BarEntry(1f, dao.day_call_use_frequency.toFloat()))
                        dataList.add(BarEntry(0f, dao.night_call_use_frequency.toFloat()))

                        val maxValue = getAxisLeftMax(dao.day_call_use_frequency, dao.night_call_use_frequency)
                        chart.setAxisLeft(ChartUtil.getLabelCount(maxValue.first), 0f, maxValue.second)
                    }
                    BaseConstants.ANALYSIS_TYPE_LIGHT -> {

                        dataList.add(BarEntry(1f, dao.day_light_exposure.toFloat()))
                        dataList.add(BarEntry(0f, dao.night_light_exposure.toFloat()))

                        // todo: 주소 값이 복사 되는 이슈 -> 깊은 복사 변경 필요
                        val maxValue = getAxisLeftMax(dao.day_light_exposure, dao.night_light_exposure)
                        chart.setAxisLeft(ChartUtil.getLabelCount(maxValue.first), 0f, maxValue.second)
                    }
                }

                return dataList
            }
        }

        /**
         * Get AxisLeft Max Value
         *
         * @param day
         * @param night
         * @return Pair<Float, Float> (max, yMax)
         */
        private fun getAxisLeftMax(day: Double, night: Double): Pair<Float, Float> {

            val max = if (day > night) ceil(day).toFloat() else ceil(night).toFloat()
            val yMax = if (max < 2f) 2f else max

            return Pair(max, yMax)
        }
    }
}