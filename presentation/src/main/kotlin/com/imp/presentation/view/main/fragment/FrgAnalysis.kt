package com.imp.presentation.view.main.fragment

import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.imp.domain.model.AnalysisModel
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgAnalysisBinding
import com.imp.presentation.view.adapter.AnalysisListAdapter
import com.imp.presentation.view.dialog.DatePickerBottomSheet
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.viewmodel.AnalysisViewModel
import com.imp.presentation.widget.extension.toGoneOrVisible
import com.imp.presentation.widget.extension.toVisibleOrGone
import com.imp.presentation.widget.utils.DateUtil
import com.imp.presentation.widget.utils.PreferencesUtil
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.shape.DotPoints
import com.kakao.vectormap.shape.PolygonOptions
import java.util.Calendar


/**
 * Main - Analysis Fragment
 */
class FrgAnalysis: BaseFragment<FrgAnalysisBinding>() {

    /** Analysis ViewModel */
    private val viewModel: AnalysisViewModel by activityViewModels()

    /** Analysis List Adapter */
    private lateinit var analysisAdapter: AnalysisListAdapter

    /** 지도 관련 변수 */
    private var kakaoMap: KakaoMap? = null

    /** Date Picker Bottom Sheet */
    private var datePickerBottomSheet: DatePickerBottomSheet? = null

    /** API 조회 Calendar */
    private var calendar: Calendar = Calendar.getInstance()

    /** DropDown Object Animator */
    private var objectAnimator: ObjectAnimator? = null

    /** Polygon Color List */
    private val polygonColorList = arrayListOf(
        R.color.color_3377ff_alpha_60,
        R.color.color_7ba7ff_alpha_60,
        R.color.color_bdd3ff_alpha_60
    )

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgAnalysisBinding.inflate(inflater, container, false)

    override fun initData() {

        // 하루 전날로 설정
        calendar.add(Calendar.DAY_OF_MONTH, -1)
    }

    override fun initView() {

        // set status bar color
        activity?.let { if (it is ActMain) it.setCurrentStatusBarColor(BaseConstants.MAIN_NAV_LABEL_LOG) }

        initObserver()
        initDisplay()
        initViewPager()
        initMapView()
        setOnClickListener()
    }

    override fun onResume() {
        super.onResume()

        // analysis list api 호출
        callAnalysisApi(calendar)
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Analysis Data */
        viewModel.analysisData.observe(this) {

            controlContentsView(true)
            updateUI(it)
            analysisAdapter.setAnalysisData(it)
        }

        /** Gps Point List */
        viewModel.pointList.observe(this) { list ->

            kakaoMap?.let { addAreaPolygon(it, list) }
        }

        /** Error Callback */
        viewModel.errorCallback.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { error ->

                controlContentsView(false)
                //context?.let { Toast.makeText(it, error.message, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // Header
            incHeader.tvTitle.text = getString(R.string.navigation_analysis)
            incHeader.ivAddChat.visibility = View.GONE

            // Date
            tvDate.text = DateUtil.getDateWithYearMonthDay(calendar)

            // 데이터 없음
            tvNoneData.text = getString(R.string.analysis_text_1)

            // todo
            // 장소의 다양성
            tvMapTitle.text = "장소의 다양성"
        }
    }

    /**
     * Initialize ViewPager
     */
    private fun initViewPager() {

        context?.let { ctx ->

            with(mBinding) {

                viewPager.apply {

                    val list = arrayListOf(
                        Pair(BaseConstants.ANALYSIS_TYPE_SCREEN_TIME, getString(R.string.log_text_1)),
                        Pair(BaseConstants.ANALYSIS_TYPE_SCREEN_AWAKE, getString(R.string.log_text_2)),
                        Pair(BaseConstants.ANALYSIS_TYPE_STEP, getString(R.string.log_text_3)),
                        Pair(BaseConstants.ANALYSIS_TYPE_CALL_TIME, getString(R.string.log_text_8)),
                        Pair(BaseConstants.ANALYSIS_TYPE_CALL_FREQUENCY, getString(R.string.log_text_9)),
                        Pair(BaseConstants.ANALYSIS_TYPE_LIGHT, getString(R.string.analysis_text_2))
                    )

                    analysisAdapter = AnalysisListAdapter(ctx, list, AnalysisModel())
                    adapter = analysisAdapter
                    orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                        }
                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        }
                    })

                    // indicator 적용
                    indicator.attachTo(this)
                }
            }
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 날짜
            llDate.setOnClickListener { openDatePickerBottomSheet() }
        }
    }

    /**
     * Initialize Map View
     */
    private fun initMapView() {

        with(mBinding) {

            // 이동 경로 map
            mapView.start(object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}
                override fun onMapError(p0: Exception?) {}
            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {

                    kakaoMap = map

                    // add polygon
                    viewModel.pointList.value?.let { addAreaPolygon(map, it) }
                }
            })
        }
    }

    /**
     * Update UI
     *
     * @param dao
     */
    private fun updateUI(dao: AnalysisModel) {

        with(mBinding) {

            // todo
            // 집에 머무른 시간
            tvHomeTitle.text = "집에 얼마나 있었게요?: ${dao.home_stay_percentage * 100}%"

            // 생활의 규칙성
            val regularity = dao.life_routine_consistency * 3
            tvRegularity.text = "나는 이만큼 규칙적이에요: ${String.format("%.1f", regularity)}/3"

            // 장소의 다양성
            var placeText = "나는 여기에 이만큼 머물렀어요"
            dao.place_diversity.forEachIndexed { index, place ->

                if (place.size > 2) {
                    placeText += "\n${index + 1}위: $index 번째 장소, ${place.first()}%"
                }
            }
            tvPlace.text = placeText
        }
    }

    /**
     * Set Area Polygon
     *
     * @param map
     * @param list
     */
    private fun addAreaPolygon(map: KakaoMap, list: ArrayList<LatLng>) {

        map.shapeManager?.layer?.removeAll()

        if (list.isEmpty()) return

        // polygon 추가
        list.forEachIndexed { index, point ->
            map.shapeManager?.layer?.addPolygon(getCircleOptions(index, point, 100))
            map.labelManager?.layer?.addLabel(LabelOptions.from(point).setStyles(R.drawable.icon_map_marker))
        }

        // 모든 좌표가 보이도록 카메라 위치 변경
        map.moveCamera(
            CameraUpdateFactory.fitMapPoints(list.toTypedArray(), 100),
            CameraAnimation.from(500, true, true)
        )
    }

    /**
     * Get Circle Polygon Option
     *
     * @param center
     * @param radius
     * @return
     */
    private fun getCircleOptions(position: Int, center: LatLng, radius: Int): PolygonOptions {

        val color = context?.let {
            ContextCompat.getColor(it, polygonColorList[position])
        } ?: Color.parseColor("#993377FF")

        return PolygonOptions.from(
            DotPoints.fromCircle(center, radius.toFloat()),
            color
        )
    }

    /**
     * Control Contents View
     *
     * @param existData
     */
    private fun controlContentsView(existData: Boolean) {

        with(mBinding) {

            ctNoneData.visibility = existData.toGoneOrVisible()
            ctAnalysisContents.visibility = existData.toVisibleOrGone()
        }
    }

    /**
     * Open Data Picker BottomSheet
     */
    private fun openDatePickerBottomSheet() {

        // open animation
        controlDropDownAnimation(true)

        datePickerBottomSheet?.dismiss()
        datePickerBottomSheet = DatePickerBottomSheet(calendar, true, { newCalendar ->

            // calendar 초기화
            calendar.time = newCalendar.time

            // Date
            mBinding.tvDate.text = DateUtil.getDateWithYearMonthDay(calendar)

            // log data api 호출
            callAnalysisApi(calendar)

        }, {

            // close animation
            controlDropDownAnimation(false)
        })
        datePickerBottomSheet?.show(childFragmentManager, "")
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

    /**
     * Call Analysis API
     *
     * @param calendar
     */
    private fun callAnalysisApi(calendar: Calendar) {

        context?.let { ctx ->

            val id = PreferencesUtil.getPreferencesString(ctx, PreferencesUtil.AUTO_LOGIN_ID_KEY)
            viewModel.loadData(id, DateUtil.calendarToServerFormat(calendar))
        }
    }
}