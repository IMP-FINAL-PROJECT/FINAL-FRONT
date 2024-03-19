package com.imp.presentation.view.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.LineData
import com.imp.domain.model.LogModel
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgLogBinding
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.viewmodel.LogViewModel
import com.imp.presentation.widget.component.CommonMapView
import com.imp.presentation.widget.extension.toDp
import com.imp.presentation.widget.utils.ChartUtil
import com.imp.presentation.widget.utils.DateUtil
import com.imp.presentation.widget.utils.MethodStorageUtil
import net.daum.mf.map.api.MapPoint

/**
 * Main - Log Fragment
 */
class FrgLog: BaseFragment<FrgLogBinding>() {

    /** Log ViewModel */
    private val viewModel: LogViewModel by activityViewModels()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgLogBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        // set status bar color
        activity?.let { if (it is ActMain) it.setCurrentStatusBarColor(BaseConstants.MAIN_NAV_LABEL_LOG) }

        initObserver()
        initDisplay()

        viewModel.loadData()
    }

    override fun onDestroy() {
        //removeMapView()
        super.onDestroy()
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Log Data */
        viewModel.logData.observe(viewLifecycleOwner) { setLogGraph(it) }

        /** Location Map Point List */
        viewModel.pointList.observe(viewLifecycleOwner) { addMapView(it) }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // Header
            incHeader.tvTitle.text = getString(R.string.navigation_log)
            incHeader.ivAddChat.visibility = View.GONE

            // Date
            tvData.text = DateUtil.getCurrentDateWithText("yyyy년 MM월 dd일")

            /**
             * 스크린 타임 그래프 초기화
             */
            incScreenTime.apply {

                tvTitle.text = getString(R.string.log_text_1)
                tvAwakeTitle.text = getString(R.string.log_text_2)

                /** 스크린 타임 */
                incChartTime.tvDate.text = DateUtil.getCurrentMonthDay()
                incChartTime.chart.animateY(300)

                /** 화면 깨우기 */
                incChartAwake.tvDate.text = DateUtil.getCurrentMonthDay()
                incChartAwake.chart.animateY(300)
            }

            /**
             * 걸음 수 그래프 초기화
             */
            incStep.apply {

                tvTitle.text = getString(R.string.log_text_3)

                incChartStep.tvDate.text = DateUtil.getCurrentMonthDay()
                incChartStep.chart.animateY(300)
            }

            /**
             * 조도 센서 그래프 초기화
             */
            incLight.apply {

                tvTitle.text = getString(R.string.log_text_4)

                tvDate.text = DateUtil.getCurrentMonthDay()
                tvSummary.visibility = View.GONE

                chart.animateY(300)
            }

            /**
             * 이동 경로 맵 초기화
             */
            incMap.apply {

                tvTitle.text = getString(R.string.log_text_5)

                tvDate.text = DateUtil.getCurrentMonthDay()
            }
        }
    }

    private fun setLogGraph(data: LogModel) {

        context?.let { ctx ->

            with(mBinding) {

                /**
                 * 스크린 타임 그래프
                 */
                incScreenTime.apply {

                    // 스크린 타임
                    incChartTime.apply {

                        tvSummary.text = getString(R.string.unit_hour_minute, 3, 47)
                        tvSummary.textSize = 24f

                        MethodStorageUtil.setSpannable(tvSummary, 1, 3, 11.toDp(ctx).toInt(), R.font.suit_meduim)
                        MethodStorageUtil.setSpannable(tvSummary, tvSummary.length() - 1, tvSummary.length(), 11.toDp(ctx).toInt(), R.font.suit_meduim)

                        val barDataSet = ChartUtil.barChartDataSet(
                            requireContext(),
                            ChartUtil.mappingBarEntryData(data.screenTime.valueList)
                        )
                        chart.data = BarData(barDataSet)
                        chart.setAxisLeft(2, 0f, data.screenTime.max)
                        chart.invalidate()
                    }

                    // 화면 깨우기
                    incChartAwake.apply {

                        tvSummary.text = getString(R.string.unit_times, 61)

                        MethodStorageUtil.setSpannable(tvSummary, tvSummary.length() - 1, tvSummary.length(), 11.toDp(ctx).toInt(), R.font.suit_meduim)

                        val barDataSet = ChartUtil.barChartDataSet(
                            requireContext(),
                            ChartUtil.mappingBarEntryData(data.screenAwake.valueList)
                        )
                        chart.data = BarData(barDataSet)
                        chart.setAxisLeft(ChartUtil.getLabelCount(data.screenAwake.max), 0f, data.screenAwake.max)
                        chart.invalidate()
                    }
                }

                /**
                 * 걸음 수 그래프
                 */
                incStep.apply {

                    incChartStep.apply {

                        tvSummary.text = getString(R.string.unit_steps, "4,901")

                        MethodStorageUtil.setSpannable(tvSummary, tvSummary.length() - 2, tvSummary.length(), 11.toDp(ctx).toInt(), R.font.suit_meduim)

                        val barDataSet = ChartUtil.barChartDataSet(
                            requireContext(),
                            ChartUtil.mappingBarEntryData(data.step.valueList)
                        )
                        chart.data = BarData(barDataSet)
                        chart.setAxisLeft(ChartUtil.getLabelCount(data.step.max), 0f, data.step.max)
                        chart.invalidate()
                    }
                }

                /**
                 * 조도 센서 그래프
                 */
                incLight.apply {

                    val lineDataset = ChartUtil.lineChartDataSet(
                        requireContext(),
                        ChartUtil.mappingEntryData(data.light.valueList)
                    )
                    chart.data = LineData(lineDataset)
                    chart.setAxisLeft(ChartUtil.getLabelCount(data.light.max), 0f, data.light.max)
                    chart.invalidate()
                }
            }
        }
    }

    /**
     * Add Map View
     */
    private fun addMapView(pointList: ArrayList<MapPoint>) {

        context?.let { ctx ->

            with(mBinding.incMap) {

                val mapView = CommonMapView(ctx).apply {

                    // add polyLine
                    addPolyline(pointList)

                    // add marker
                    addMarker(pointList)
                }

                clMapContainer.addView(mapView)
            }
        }
    }

    /**
     * Remove Map View
     *   - 2개 이상의 Kakao MapView를 추가할 수 없음
     */
    private fun removeMapView() {
        mBinding.incMap.clMapContainer.removeAllViews()
    }
}