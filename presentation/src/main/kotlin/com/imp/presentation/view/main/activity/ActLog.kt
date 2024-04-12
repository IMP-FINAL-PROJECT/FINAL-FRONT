package com.imp.presentation.view.main.activity

import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.LineData
import com.imp.domain.model.LogModel
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActLogBinding
import com.imp.presentation.viewmodel.LogViewModel
import com.imp.presentation.widget.extension.toDp
import com.imp.presentation.widget.utils.ChartUtil
import com.imp.presentation.widget.utils.DateUtil
import com.imp.presentation.widget.utils.MethodStorageUtil
import com.imp.presentation.widget.utils.PreferencesUtil
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet
import dagger.hilt.android.AndroidEntryPoint

/**
 * Log Activity
 */
@AndroidEntryPoint
class ActLog : BaseContractActivity<ActLogBinding>() {

    /** Log ViewModel */
    private val viewModel: LogViewModel by viewModels()

    override fun getViewBinding() = ActLogBinding.inflate(layoutInflater)

    override fun initData() {

    }

    override fun initView() {

        initObserver()
        initDisplay()
        initMapView()
        setOnClickListener()

        // log data api 호출
        val id = PreferencesUtil.getPreferencesString(this@ActLog, PreferencesUtil.AUTO_LOGIN_ID_KEY)
        viewModel.loadData("dongwook@naver.com")
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Log Data */
        viewModel.logData.observe(this) { setLogGraph(it) }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // Header
            incHeader.tvTitle.text = getString(R.string.navigation_log)

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
                initWeekBtn(incChartTime.tvDay, incChartTime.tvWeek, {

                    incChartTime.tvDate.text = DateUtil.getCurrentMonthDay()
                    viewModel.logData.value?.let { setScreenTimeGraph(it, isDay = true) }
                }, {

                    incChartTime.tvDate.text = DateUtil.getCurrentWeekly()
                    viewModel.logData.value?.let { setScreenTimeGraph(it, isDay = false) }
                })

                /** 화면 깨우기 */
                incChartAwake.tvDate.text = DateUtil.getCurrentMonthDay()
                incChartAwake.chart.animateY(300)
                initWeekBtn(incChartAwake.tvDay, incChartAwake.tvWeek, {

                    incChartAwake.tvDate.text = DateUtil.getCurrentMonthDay()
                    viewModel.logData.value?.let { setScreenAwakeGraph(it, isDay = true) }
                }, {

                    incChartAwake.tvDate.text = DateUtil.getCurrentWeekly()
                    viewModel.logData.value?.let { setScreenAwakeGraph(it, isDay = false) }
                })
            }

            /**
             * 걸음 수 그래프 초기화
             */
            incStep.apply {

                tvTitle.text = getString(R.string.log_text_3)

                incChartStep.tvDate.text = DateUtil.getCurrentMonthDay()
                incChartStep.chart.animateY(300)
                initWeekBtn(incChartStep.tvDay, incChartStep.tvWeek, {

                    incChartStep.tvDate.text = DateUtil.getCurrentMonthDay()
                    viewModel.logData.value?.let { setStepGraph(it, isDay = true) }
                }, {

                    incChartStep.tvDate.text = DateUtil.getCurrentWeekly()
                    viewModel.logData.value?.let { setStepGraph(it, isDay = false) }
                })
            }

            /**
             * 조도 센서 그래프 초기화
             */
            incLight.apply {

                tvTitle.text = getString(R.string.log_text_4)

                tvDate.text = DateUtil.getCurrentMonthDay()
                tvSummary.visibility = View.GONE

                chart.animateY(300)
                initWeekBtn(tvDay, tvWeek, {

                    tvDate.text = DateUtil.getCurrentMonthDay()
                    viewModel.logData.value?.let { setLightGraph(it, isDay = true) }
                }, {

                    tvDate.text = DateUtil.getCurrentWeekly()
                    viewModel.logData.value?.let { setLightGraph(it, isDay = false) }
                })
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

    /**
     * Initialize Day, Week Select Button
     *
     * @param dayView
     * @param weekView
     * @param dayClickEvent
     * @param weekClickEvent
     */
    private fun initWeekBtn(dayView: TextView, weekView: TextView, dayClickEvent: () -> Unit, weekClickEvent: () -> Unit) {

        dayView.text = getString(R.string.log_text_6)
        weekView.text = getString(R.string.log_text_7)

        // 1일 클릭 이벤트
        dayView.isSelected = true
        dayView.setOnClickListener {

            if (it.isSelected) return@setOnClickListener

            it.isSelected = !it.isSelected
            weekView.isSelected = false

            dayClickEvent.invoke()
        }

        // 1주일 클릭 이벤트
        weekView.isSelected = false
        weekView.setOnClickListener {

            if (it.isSelected) return@setOnClickListener

            it.isSelected = !it.isSelected
            dayView.isSelected = false

            weekClickEvent.invoke()
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 뒤로가기
            incHeader.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    /**
     * Initialize Map View
     */
    private fun initMapView() {

        with(mBinding.incMap) {

            // 이동 경로 map
            mapView.start(object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}
                override fun onMapError(p0: Exception?) {}
            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {

                    // add points
                    viewModel.pointList.value?.let { addPoints(map, it) }
                }
            })
        }
    }

    /**
     * 로그 그래프
     *
     * @param data
     */
    private fun setLogGraph(data: LogModel) {

        setScreenTimeGraph(data, isDay = true)
        setScreenAwakeGraph(data, isDay = true)
        setStepGraph(data, isDay = true)
        setLightGraph(data, isDay = true)
    }

    /**
     * 스크린 타임 그래프
     *
     * @param data
     * @param isDay
     */
    private fun setScreenTimeGraph(data: LogModel, isDay: Boolean) {

        mBinding.incScreenTime.incChartTime.apply {

            tvSummary.text = getString(R.string.unit_hour_minute, 3, 47)
            tvSummary.textSize = 24f

            MethodStorageUtil.setSpannable(tvSummary, 1, 3, 11.toDp(this@ActLog).toInt(), R.font.suit_medium)
            MethodStorageUtil.setSpannable(tvSummary, tvSummary.length() - 1, tvSummary.length(), 11.toDp(this@ActLog).toInt(), R.font.suit_medium)

            val dataList = if (isDay) data.day.screen_duration_list else data.week.screen_duration_list
            val barDataSet = ChartUtil.barChartDataSet(this@ActLog, ChartUtil.mappingBarEntryData(dataList.toFloatArray()))

            chart.data = BarData(barDataSet).apply { barWidth = if (isDay) 0.7f else 0.2f }
            chart.setAxisLeft(2, 0f, dataList.max().toFloat())
            chart.setChartWeek(isDay)
            chart.invalidate()
        }
    }

    private fun ArrayList<Int>.toFloatArray(): ArrayList<Float> {

        val list = ArrayList<Float>()
        this.forEach { list.add(it.toFloat()) }

        return list
    }

    /**
     * 화면 깨우기 그래프
     *
     * @param data
     * @param isDay
     */
    private fun setScreenAwakeGraph(data: LogModel, isDay: Boolean) {

        mBinding.incScreenTime.incChartAwake.apply {

            tvSummary.text = getString(R.string.unit_times, 61)

            MethodStorageUtil.setSpannable(tvSummary, tvSummary.length() - 1, tvSummary.length(), 11.toDp(this@ActLog).toInt(), R.font.suit_medium)

            val dataList = if (isDay) data.day.screen_frequency_list else data.week.screen_frequency_list
            val barDataSet = ChartUtil.barChartDataSet(this@ActLog, ChartUtil.mappingBarEntryData(dataList.toFloatArray()))

            chart.data = BarData(barDataSet).apply { barWidth = if (isDay) 0.7f else 0.2f }
            chart.setAxisLeft(ChartUtil.getLabelCount(dataList.max().toFloat()), 0f, dataList.max().toFloat())
            chart.setChartWeek(isDay)
            chart.invalidate()
        }
    }

    /**
     * 걸음 수 그래프
     *
     * @param data
     * @param isDay
     */
    private fun setStepGraph(data: LogModel, isDay: Boolean) {

        mBinding.incStep.incChartStep.apply {

            tvSummary.text = getString(R.string.unit_steps, "4,901")

            MethodStorageUtil.setSpannable(tvSummary, tvSummary.length() - 2, tvSummary.length(), 11.toDp(this@ActLog).toInt(), R.font.suit_medium)

            val dataList = if (isDay) data.day.pedometer_list else data.week.pedometer_list
            val barDataSet = ChartUtil.barChartDataSet(this@ActLog, ChartUtil.mappingBarEntryData(dataList.toFloatArray()))

            chart.data = BarData(barDataSet).apply { barWidth = if (isDay) 0.7f else 0.2f }
            chart.setAxisLeft(ChartUtil.getLabelCount(dataList.max().toFloat()), 0f, dataList.max().toFloat())
            chart.setChartWeek(isDay)
            chart.invalidate()
        }
    }

    /**
     * 조도 센서 그래프
     *
     * @param data
     * @param isDay
     */
    private fun setLightGraph(data: LogModel, isDay: Boolean) {

        mBinding.incLight.apply {

            val dataList = if (isDay) data.day.illuminance else data.week.illuminance
            val lineDataset = ChartUtil.lineChartDataSet(this@ActLog, ChartUtil.mappingEntryData(dataList.toFloatArray()))

            chart.data = LineData(lineDataset)
            chart.setAxisLeft(ChartUtil.getLabelCount(dataList.max().toFloat()), 0f, dataList.max().toFloat())
            chart.setChartWeek(isDay)
            chart.invalidate()
        }
    }

    /**
     * Add Points
     *
     * @param map
     * @param list
     */
    private fun addPoints(map: KakaoMap, list: ArrayList<LatLng>) {

        // label 추가
        list.forEach { location ->
            map.labelManager?.layer?.addLabel(LabelOptions.from(location).setStyles(R.drawable.icon_map_marker))
        }

        // 모든 좌표가 보이도록 카메라 위치 변경
        map.moveCamera(
            CameraUpdateFactory.fitMapPoints(list.toTypedArray(), 100),
            CameraAnimation.from(500, true, true)
        )

        // line 추가
        val stylesSet = RouteLineStylesSet.from("blueStyles", RouteLineStyles.from(RouteLineStyle.from(4f, ContextCompat.getColor(this@ActLog, R.color.color_3377ff))))
        val segment = RouteLineSegment.from(list).setStyles(stylesSet.getStyles(0))
        val options = RouteLineOptions.from(segment).setStylesSet(stylesSet)

        map.routeLineManager?.layer?.addRouteLine(options)
    }
}