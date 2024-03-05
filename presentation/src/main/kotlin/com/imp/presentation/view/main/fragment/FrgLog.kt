package com.imp.presentation.view.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.databinding.FrgLogBinding
import com.imp.presentation.widget.utils.ChartUtil
import kotlin.random.Random

/**
 * Main - Log Fragment
 */
class FrgLog: BaseFragment<FrgLogBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgLogBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        initScreenTime()
        initStep()
        initLight()
    }

    /**
     * 스크린 타임 그래프 초기화
     */
    private fun initScreenTime() {

        with(mBinding.incScreenTime) {

            tvTitle.text = "스크린 타임"
            tvAwakeTitle.text = "화면 깨우기"

            /** 스크린 타임 */
            incChartTime.apply {

                tvDate.text = "오늘, 3월 4일"
                tvSummary.text = "3시간 47분"

                chart.apply {

                    setRadius(4)
                    animateY(300)
                    setAxisLeft(2, 0f, 60f)

                    val barDataSet = ChartUtil.barChartDataSet(context, getData(61))
                    val chartData = BarData(barDataSet)
                    data = chartData
                    invalidate()
                }
            }

            /** 화면 깨우기 */
            incChartAwake.apply {

                tvDate.text = "오늘, 3월 4일"
                tvSummary.text = "61번"

                chart.apply {

                    setRadius(4)
                    animateY(300)
                    setAxisLeft(2, 0f, 10f)

                    val barDataSet = ChartUtil.barChartDataSet(context, getData(10))
                    val chartData = BarData(barDataSet)
                    data = chartData
                    invalidate()
                }
            }
        }
    }

    /**
     * 걸음 수 그래프 초기화
     */
    private fun initStep() {

        with(mBinding.incStep) {

            tvTitle.text = "걸음"

            /** 걸음 */
            incChartStep.apply {

                tvDate.text = "오늘, 3월 4일"
                tvSummary.text = "4,901걸음"

                chart.apply {

                    setRadius(4)
                    animateY(300)
                    setAxisLeft(5, 0f, 1000f)

                    val barDataSet = ChartUtil.barChartDataSet(context, getData(1000))
                    val chartData = BarData(barDataSet)
                    data = chartData
                    invalidate()
                }
            }
        }
    }

    /**
     * 조도 센서 그래프 초기화
     */
    private fun initLight() {

    }

    private fun getData(max: Int): ArrayList<BarEntry> {

        val entries = ArrayList<BarEntry>()

        for (i in 0 until 24) {
            entries.add(BarEntry(i.toFloat(), Random.Default.nextInt(max).toFloat()))
        }

        return entries
    }
}