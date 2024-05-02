package com.imp.presentation.view.main.activity

import android.animation.ObjectAnimator
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.LineData
import com.imp.domain.model.LogModel
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActLogBinding
import com.imp.presentation.view.dialog.DatePickerBottomSheet
import com.imp.presentation.viewmodel.LogViewModel
import com.imp.presentation.widget.extension.setMaxSize
import com.imp.presentation.widget.extension.thousandUnits
import com.imp.presentation.widget.extension.toDp
import com.imp.presentation.widget.extension.toFloatArray
import com.imp.presentation.widget.extension.toMinuteList
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
import java.util.Calendar

/**
 * Log Activity
 */
@AndroidEntryPoint
class ActLog : BaseContractActivity<ActLogBinding>() {

    /** Log ViewModel */
    private val viewModel: LogViewModel by viewModels()

    /** Kakao Map */
    private var kakaoMap: KakaoMap? = null

    /** Gps Point 추가 여부 */
    private var addPoints: Boolean = false

    /** Day, Week TextView List */
    private var dayViewList: ArrayList<TextView> = ArrayList()
    private var weekViewList: ArrayList<TextView> = ArrayList()

    /** Date Picker Bottom Sheet */
    private var datePickerBottomSheet: DatePickerBottomSheet? = null

    /** API 조회 Calendar */
    private var calendar: Calendar = Calendar.getInstance()

    /** DropDown Object Animator */
    private var objectAnimator: ObjectAnimator? = null

    override fun getViewBinding() = ActLogBinding.inflate(layoutInflater)

    override fun initData() {

        dayViewList.clear()
        weekViewList.clear()
    }

    override fun initView() {

        initObserver()
        initDisplay()
        initMapView()
        setOnClickListener()

        // log data api 호출
        val id = PreferencesUtil.getPreferencesString(this@ActLog, PreferencesUtil.AUTO_LOGIN_ID_KEY)
        viewModel.loadData(id, DateUtil.calendarToServerFormat(calendar))
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Log Data */
        viewModel.logData.observe(this) {

            setLogGraph(it)
            resetDayAndWeekClickEvent()
        }

        /** Gps Point List */
        viewModel.pointList.observe(this) { if (kakaoMap != null) addPoints(kakaoMap!!, it) }

        /** Error Callback */
        viewModel.errorCallback.observe(this) { event ->
            event.getContentIfNotHandled()?.let { error ->

                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // Header
            incHeader.tvTitle.text = getString(R.string.log_text_10)

            // Date
            tvDate.text = DateUtil.getDateWithYearMonthDay(calendar)

            /**
             * 스크린 타임 그래프 초기화
             */
            incScreenTime.apply {

                tvTitle.text = getString(R.string.log_text_1)
                tvAwakeTitle.text = getString(R.string.log_text_2)

                /** 스크린 타임 */
                incChartTime.chart.animateY(300)
                initWeekBtn(incChartTime.tvDay, incChartTime.tvWeek, {

                    viewModel.logData.value?.let { setScreenTimeGraph(it, isDay = true) }
                }, {

                    viewModel.logData.value?.let { setScreenTimeGraph(it, isDay = false) }
                })

                /** 화면 깨우기 */
                incChartCount.chart.animateY(300)
                initWeekBtn(incChartCount.tvDay, incChartCount.tvWeek, {

                    viewModel.logData.value?.let { setScreenAwakeGraph(it, isDay = true) }
                }, {

                    viewModel.logData.value?.let { setScreenAwakeGraph(it, isDay = false) }
                })
            }

            /**
             * 걸음 수 그래프 초기화
             */
            incStep.apply {

                tvTitle.text = getString(R.string.log_text_3)

                incChartStep.chart.animateY(300)
                initWeekBtn(incChartStep.tvDay, incChartStep.tvWeek, {

                    viewModel.logData.value?.let { setStepGraph(it, isDay = true) }
                }, {

                    viewModel.logData.value?.let { setStepGraph(it, isDay = false) }
                })
            }

            /**
             * 통화 시간 그래프 초기화
             */
            incCallTime.apply {

                tvTitle.text = getString(R.string.log_text_8)
                tvAwakeTitle.text = getString(R.string.log_text_9)

                /** 통화 시간 */
                incChartTime.chart.animateY(300)
                initWeekBtn(incChartTime.tvDay, incChartTime.tvWeek, {

                    viewModel.logData.value?.let { setCallTimeGraph(it, isDay = true) }
                }, {

                    viewModel.logData.value?.let { setCallTimeGraph(it, isDay = false) }
                })

                /** 통화 횟수 */
                incChartCount.tvDate.text = DateUtil.getCurrentMonthDay()
                incChartCount.chart.animateY(300)
                initWeekBtn(incChartCount.tvDay, incChartCount.tvWeek, {

                    viewModel.logData.value?.let { setCallCountGraph(it, isDay = true) }
                }, {

                    viewModel.logData.value?.let { setCallCountGraph(it, isDay = false) }
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

                    viewModel.logData.value?.let { setLightGraph(it, isDay = true) }
                }, {

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

        dayViewList.add(dayView)
        weekViewList.add(weekView)

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

            // 뒤로 가기
            incHeader.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            // 날짜
            llDate.setOnClickListener { openDatePickerBottomSheet() }
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

                    kakaoMap = map

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
        setCallTimeGraph(data, isDay = true)
        setCallCountGraph(data, isDay = true)
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

            // 날짜 변경
            tvDate.text = if (isDay) {
                DateUtil.getMonthDay(calendar)
            } else {
                DateUtil.getWeeklyWithLast(calendar)
            }

            // 요약
            val summary = if (isDay) data.day.screen_duration else data.day.screen_duration
            val screenTime = DateUtil.timestampToScreenTime(summary.toLong())

            tvSummary.text = getString(R.string.unit_hour_minute, screenTime.first, screenTime.second)
            tvSummary.textSize = 24f

            MethodStorageUtil.setSpannable(tvSummary, screenTime.first.toString().length, screenTime.first.toString().length + 2, 11.toDp(this@ActLog).toInt(), R.font.suit_medium)
            MethodStorageUtil.setSpannable(tvSummary, tvSummary.length() - 1, tvSummary.length(), 11.toDp(this@ActLog).toInt(), R.font.suit_medium)

            // 차트
            val dataList = if (isDay) data.day.screen_duration_list.toMinuteList() else data.week.screen_duration_list.toMinuteList()
            val barDataSet = ChartUtil.barChartDataSet(this@ActLog, ChartUtil.mappingBarEntryData(dataList.toFloatArray().setMaxSize(isDay)))

            chart.data = BarData(barDataSet).apply { barWidth = if (isDay) 0.7f else 0.2f }
            chart.setAxisLeft(2, 0f, 60f)
            chart.setChartWeek(isDay, calendar)
            chart.invalidate()
        }
    }

    /**
     * 화면 깨우기 그래프
     *
     * @param data
     * @param isDay
     */
    private fun setScreenAwakeGraph(data: LogModel, isDay: Boolean) {

        mBinding.incScreenTime.incChartCount.apply {

            // 날짜 변경
            tvDate.text = if (isDay) {
                DateUtil.getMonthDay(calendar)
            } else {
                DateUtil.getWeeklyWithLast(calendar)
            }

            // 요약
            val summary = if (isDay) data.day.screen_frequency else data.day.screen_frequency
            tvSummary.text = getString(R.string.unit_times, summary)

            MethodStorageUtil.setSpannable(tvSummary, tvSummary.length() - 1, tvSummary.length(), 11.toDp(this@ActLog).toInt(), R.font.suit_medium)

            // 차트
            val dataList = if (isDay) data.day.screen_frequency_list else data.week.screen_frequency_list
            val barDataSet = ChartUtil.barChartDataSet(this@ActLog, ChartUtil.mappingBarEntryData(dataList.toFloatArray().setMaxSize(isDay)))

            val max = if (dataList.isEmpty()) 0f else dataList.max().toFloat()
            val yMax = if (max < 2f) 2f else max

            chart.data = BarData(barDataSet).apply { barWidth = if (isDay) 0.7f else 0.2f }
            chart.setAxisLeft(ChartUtil.getLabelCount(max), 0f, yMax)
            chart.setChartWeek(isDay, calendar)
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

            // 날짜 변경
            tvDate.text = if (isDay) {
                DateUtil.getMonthDay(calendar)
            } else {
                DateUtil.getWeeklyWithLast(calendar)
            }

            // 요약
            val summary = if (isDay) data.day.pedometer else data.day.pedometer
            tvSummary.text = getString(R.string.unit_steps, summary.thousandUnits())

            MethodStorageUtil.setSpannable(tvSummary, tvSummary.length() - 2, tvSummary.length(), 11.toDp(this@ActLog).toInt(), R.font.suit_medium)

            // 차트
            val dataList = if (isDay) data.day.pedometer_list else data.week.pedometer_list
            val barDataSet = ChartUtil.barChartDataSet(this@ActLog, ChartUtil.mappingBarEntryData(dataList.toFloatArray().setMaxSize(isDay)))

            val max = if (dataList.isEmpty()) 0f else dataList.max().toFloat()
            val yMax = if (max < 2f) 2f else max

            chart.data = BarData(barDataSet).apply { barWidth = if (isDay) 0.7f else 0.2f }
            chart.setAxisLeft(ChartUtil.getLabelCount(max), 0f, yMax)
            chart.setChartWeek(isDay, calendar)
            chart.invalidate()
        }
    }

    /**
     * 통화 시간 그래프
     *
     * @param data
     * @param isDay
     */
    private fun setCallTimeGraph(data: LogModel, isDay: Boolean) {

        mBinding.incCallTime.incChartTime.apply {

            // 날짜 변경
            tvDate.text = if (isDay) {
                DateUtil.getMonthDay(calendar)
            } else {
                DateUtil.getWeeklyWithLast(calendar)
            }

            // 요약
            val summary = if (isDay) data.day.call_duration else data.day.call_duration
            val screenTime = DateUtil.timestampToScreenTime(summary.toLong())

            tvSummary.text = getString(R.string.unit_hour_minute, screenTime.first, screenTime.second)
            tvSummary.textSize = 24f

            MethodStorageUtil.setSpannable(tvSummary, screenTime.first.toString().length, screenTime.first.toString().length + 2, 11.toDp(this@ActLog).toInt(), R.font.suit_medium)
            MethodStorageUtil.setSpannable(tvSummary, tvSummary.length() - 1, tvSummary.length(), 11.toDp(this@ActLog).toInt(), R.font.suit_medium)

            // 차트
            val dataList = if (isDay) data.day.call_duration_list.toMinuteList() else data.week.call_duration_list.toMinuteList()
            val barDataSet = ChartUtil.barChartDataSet(this@ActLog, ChartUtil.mappingBarEntryData(dataList.toFloatArray().setMaxSize(isDay)))

            chart.data = BarData(barDataSet).apply { barWidth = if (isDay) 0.7f else 0.2f }
            chart.setAxisLeft(2, 0f, 60f)
            chart.setChartWeek(isDay, calendar)
            chart.invalidate()
        }
    }

    /**
     * 통화 횟수 그래프
     *
     * @param data
     * @param isDay
     */
    private fun setCallCountGraph(data: LogModel, isDay: Boolean) {

        mBinding.incCallTime.incChartCount.apply {

            // 날짜 변경
            tvDate.text = if (isDay) {
                DateUtil.getMonthDay(calendar)
            } else {
                DateUtil.getWeeklyWithLast(calendar)
            }

            // 요약
            val summary = if (isDay) data.day.call_frequency else data.day.call_frequency
            tvSummary.text = getString(R.string.unit_times, summary)

            MethodStorageUtil.setSpannable(tvSummary, tvSummary.length() - 1, tvSummary.length(), 11.toDp(this@ActLog).toInt(), R.font.suit_medium)

            // 차트
            val dataList = if (isDay) data.day.call_frequency_list else data.week.call_frequency_list
            val barDataSet = ChartUtil.barChartDataSet(this@ActLog, ChartUtil.mappingBarEntryData(dataList.toFloatArray().setMaxSize(isDay)))

            val max = if (dataList.isEmpty()) 0f else dataList.max().toFloat()
            val yMax = if (max < 2f) 2f else max

            chart.data = BarData(barDataSet).apply { barWidth = if (isDay) 0.7f else 0.2f }
            chart.setAxisLeft(ChartUtil.getLabelCount(max), 0f, yMax)
            chart.setChartWeek(isDay, calendar)
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

            // 날짜 변경
            tvDate.text = if (isDay) {
                DateUtil.getMonthDay(calendar)
            } else {
                DateUtil.getWeeklyWithLast(calendar)
            }

            // 차트
            val dataList = if (isDay) data.day.illuminance else data.week.illuminance
            val lineDataset = ChartUtil.lineChartDataSet(this@ActLog, ChartUtil.mappingEntryData(dataList.toFloatArray().setMaxSize(isDay)))

            val max = if (dataList.isEmpty()) 0f else dataList.max().toFloat()
            val yMax = if (max < 2f) 2f else max

            chart.data = LineData(lineDataset)
            chart.setAxisLeft(ChartUtil.getLabelCount(max), 0f, yMax)
            chart.setChartWeek(isDay, calendar)
            chart.invalidate()
        }
    }

    /**
     * Reset Day and Week ClickEvent
     */
    private fun resetDayAndWeekClickEvent() {

        dayViewList.forEach { it.isSelected = true }
        weekViewList.forEach { it.isSelected = false }
    }

    /**
     * Add Points
     *
     * @param map
     * @param list
     */
    private fun addPoints(map: KakaoMap, list: ArrayList<LatLng>) {

        // points add 여부
        if (addPoints) return
        addPoints = true

        if (list.isEmpty()) return

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

    /**
     * Open Data Picker BottomSheet
     */
    private fun openDatePickerBottomSheet() {

        datePickerBottomSheet?.dismiss()
        datePickerBottomSheet = DatePickerBottomSheet(calendar, false, { newCalendar ->

            // calendar 초기화
            calendar.time = newCalendar.time

            // Date
            mBinding.tvDate.text = DateUtil.getDateWithYearMonthDay(calendar)

            // log data api 호출
            val id = PreferencesUtil.getPreferencesString(this@ActLog, PreferencesUtil.AUTO_LOGIN_ID_KEY)
            viewModel.loadData(id, DateUtil.calendarToServerFormat(calendar))

        }, {

            // close animation
            controlDropDownAnimation(false)
        })
        datePickerBottomSheet?.show(supportFragmentManager, "")

        // open animation
        controlDropDownAnimation(true)
    }

    /**
     * Control DropDown Animation
     *
     * @param openBottomSheet
     */
    private fun controlDropDownAnimation(openBottomSheet: Boolean) {

        with(mBinding) {

            val startAngle = if (openBottomSheet) 0f else -180f
            val endAngle = if (openBottomSheet) -180f else 0f

            objectAnimator?.cancel()
            objectAnimator = ObjectAnimator.ofFloat(tvDropDown, View.ROTATION, startAngle, endAngle).apply {
                duration = 300
                doOnStart { tvDropDown.rotation = startAngle }
                doOnEnd { tvDropDown.rotation = endAngle }
                start()
            }
        }
    }
}