package com.imp.presentation.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.imp.domain.model.AnalysisModel
import com.imp.presentation.R
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.ItemAnalysisBinding
import com.imp.presentation.widget.extension.toDp
import com.imp.presentation.widget.utils.ChartUtil
import com.imp.presentation.widget.utils.DateUtil
import com.imp.presentation.widget.utils.MethodStorageUtil
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
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

                    val dataList = getCharDataList(ctx, dao.first, analysisData)
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
        private fun getCharDataList(context: Context, type: String, dao: AnalysisModel): ArrayList<BarEntry> {

            with(binding) {

                val dataList = ArrayList<BarEntry>()
                when(type) {

                    BaseConstants.ANALYSIS_TYPE_SCREEN_TIME -> {

                        val day = DateUtil.timestampToScreenTime(dao.day_phone_use_duration.toLong())
                        val night = DateUtil.timestampToScreenTime(dao.night_phone_use_duration.toLong())

                        dataList.add(BarEntry(1f, "${day.first}.${day.second}".toFloat()))
                        dataList.add(BarEntry(0f, "${night.first}.${night.second}".toFloat()))

                        chart.setAxisLeft(4, 0f, 12f)

                        // description 설정
                        setDescriptionTime(
                            context, dao.day_phone_use_duration, dao.night_phone_use_duration,
                            R.string.analysis_text_16, R.string.analysis_text_15, R.string.analysis_text_17
                        )
                    }
                    BaseConstants.ANALYSIS_TYPE_SCREEN_AWAKE -> {

                        dataList.add(BarEntry(1f, dao.day_phone_use_frequency.toFloat()))
                        dataList.add(BarEntry(0f, dao.night_phone_use_frequency.toFloat()))

                        val maxValue = getAxisLeftMax(dao.day_phone_use_frequency, dao.night_phone_use_frequency)
                        chart.setAxisLeft(ChartUtil.getLabelCount(maxValue.first), 0f, maxValue.second)

                        // description 설정
                        setDescriptionCount(
                            context, dao.day_phone_use_frequency, dao.night_phone_use_frequency,
                            R.string.analysis_text_13, R.string.analysis_text_12, R.string.analysis_text_14
                        )
                    }
                    BaseConstants.ANALYSIS_TYPE_STEP -> {

                        dataList.add(BarEntry(1f, dao.day_step_count.toFloat()))
                        dataList.add(BarEntry(0f, dao.night_step_count.toFloat()))

                        val maxValue = getAxisLeftMax(dao.day_step_count, dao.night_step_count)
                        chart.setAxisLeft(ChartUtil.getLabelCount(maxValue.first), 0f, maxValue.second)

                        // description 설정
                        setDescriptionCount(
                            context, dao.day_step_count, dao.night_step_count,
                            R.string.analysis_text_10, R.string.analysis_text_9, R.string.analysis_text_11
                        )
                    }
                    BaseConstants.ANALYSIS_TYPE_CALL_TIME -> {

                        val day = DateUtil.timestampToScreenTime(dao.day_call_use_duration.toLong())
                        val night = DateUtil.timestampToScreenTime(dao.night_call_use_duration.toLong())

                        dataList.add(BarEntry(1f, "${day.first}.${day.second}".toFloat()))
                        dataList.add(BarEntry(0f, "${night.first}.${night.second}".toFloat()))

                        chart.setAxisLeft(6, 0f, 12f)

                        // description 설정
                        setDescriptionTime(
                            context, dao.day_call_use_duration, dao.night_call_use_duration,
                            R.string.analysis_text_19, R.string.analysis_text_18, R.string.analysis_text_20
                        )
                    }
                    BaseConstants.ANALYSIS_TYPE_CALL_FREQUENCY -> {

                        dataList.add(BarEntry(1f, dao.day_call_use_frequency.toFloat()))
                        dataList.add(BarEntry(0f, dao.night_call_use_frequency.toFloat()))

                        val maxValue = getAxisLeftMax(dao.day_call_use_frequency, dao.night_call_use_frequency)
                        chart.setAxisLeft(ChartUtil.getLabelCount(maxValue.first), 0f, maxValue.second)

                        // description 설정
                        setDescriptionCount(
                            context, dao.day_call_use_frequency, dao.night_call_use_frequency,
                            R.string.analysis_text_22, R.string.analysis_text_21, R.string.analysis_text_23
                        )
                    }
                    BaseConstants.ANALYSIS_TYPE_LIGHT -> {

                        dataList.add(BarEntry(1f, dao.day_light_exposure.toFloat()))
                        dataList.add(BarEntry(0f, dao.night_light_exposure.toFloat()))

                        // todo: 주소 값이 복사 되는 이슈 -> 깊은 복사 변경 필요
                        val maxValue = getAxisLeftMax(dao.day_light_exposure, dao.night_light_exposure)
                        chart.setAxisLeft(ChartUtil.getLabelCount(maxValue.first), 0f, maxValue.second)

                        tvDescription.visibility = View.GONE
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

        /**
         * Set Description Count
         *
         * @param context
         * @param day
         * @param night
         * @param dayStringId
         * @param nightStringId
         * @param equalStringId
         */
        private fun setDescriptionCount(context: Context, day: Double, night: Double, dayStringId: Int, nightStringId: Int, equalStringId: Int) {

            with(binding) {

                val value = day.toInt() - night.toInt()

                tvDescription.text = if (value > 0) {

                    // 오전 > 오후
                    context.getString(dayStringId, value)

                } else if (value < 0) {

                    // 오전 < 오후
                    context.getString(nightStringId, value * (-1))

                } else {

                    // 오전 == 오후
                    context.getString(equalStringId, day.toInt() + night.toInt())
                }

                if (value == 0) {

                    val count = day.toInt() + night.toInt()
                    val end = if (count == 0) 1 else count
                    MethodStorageUtil.setSpannable(tvDescription, 11, end.toString().length + 12, 16.toDp(context).toInt(), R.font.suit_extrabold, ContextCompat.getColor(context, R.color.color_3377ff))
                } else {
                    MethodStorageUtil.setSpannable(tvDescription, 7, value.toString().length + 8, 16.toDp(context).toInt(), R.font.suit_extrabold, ContextCompat.getColor(context, R.color.color_3377ff))
                }
            }
        }

        /**
         * Set Description Time
         *
         * @param context
         * @param day
         * @param night
         * @param dayStringId
         * @param nightStringId
         * @param equalStringId
         */
        private fun setDescriptionTime(context: Context, day: Double, night: Double, dayStringId: Int, nightStringId: Int, equalStringId: Int) {

            with(binding) {

                if (day > night) {

                    // 오전 > 오후
                    val time = DateUtil.timestampToScreenTime((day - night).toLong())
                    val string = context.getString(R.string.unit_hour_minute, time.first, time.second)
                    tvDescription.text = context.getString(dayStringId, string)
                    MethodStorageUtil.setSpannable(tvDescription, 7, string.length + 8, 16.toDp(context).toInt(), R.font.suit_extrabold, ContextCompat.getColor(context, R.color.color_3377ff))

                } else if (day < night) {

                    // 오전 < 오후
                    val time = DateUtil.timestampToScreenTime((night - day).toLong())
                    val string = context.getString(R.string.unit_hour_minute, time.first, time.second)
                    tvDescription.text = context.getString(nightStringId, string)
                    MethodStorageUtil.setSpannable(tvDescription, 7, string.length + 8, 16.toDp(context).toInt(), R.font.suit_extrabold, ContextCompat.getColor(context, R.color.color_3377ff))

                } else {

                    // 오전 == 오후
                    val time = DateUtil.timestampToScreenTime((day + night).toLong())
                    val string = context.getString(R.string.unit_hour_minute, time.first, time.second)
                    tvDescription.text = context.getString(equalStringId, string)
                    MethodStorageUtil.setSpannable(tvDescription, 10, string.length + 11, 16.toDp(context).toInt(), R.font.suit_extrabold, ContextCompat.getColor(context, R.color.color_3377ff))
                }
            }
        }
    }
}