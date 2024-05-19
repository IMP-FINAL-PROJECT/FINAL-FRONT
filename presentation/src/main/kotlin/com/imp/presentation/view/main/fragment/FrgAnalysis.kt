package com.imp.presentation.view.main.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.imp.domain.model.AddressModel
import com.imp.domain.model.AnalysisModel
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgAnalysisBinding
import com.imp.presentation.view.adapter.AnalysisListAdapter
import com.imp.presentation.view.dialog.AIAnalysisBottomSheet
import com.imp.presentation.view.dialog.DatePickerBottomSheet
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.viewmodel.AnalysisViewModel
import com.imp.presentation.viewmodel.MemberViewModel
import com.imp.presentation.widget.extension.setPreviewBothSide
import com.imp.presentation.widget.extension.toDp
import com.imp.presentation.widget.extension.toGoneOrVisible
import com.imp.presentation.widget.extension.toVisibleOrGone
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
import com.kakao.vectormap.shape.DotPoints
import com.kakao.vectormap.shape.PolygonOptions
import java.util.Calendar
import kotlin.math.roundToInt


/**
 * Main - Analysis Fragment
 */
class FrgAnalysis: BaseFragment<FrgAnalysisBinding>() {

    /** Analysis ViewModel */
    private val viewModel: AnalysisViewModel by activityViewModels()

    /** Member ViewModel */
    private val memberViewModel: MemberViewModel by activityViewModels()

    /** Analysis List Adapter */
    private lateinit var analysisAdapter: AnalysisListAdapter

    /** ViewPager 관련 변수 */
    private var initSetPreviewBothSide: Boolean = false

    /** 지도 관련 변수 */
    private var kakaoMap: KakaoMap? = null

    /** API 조회 Calendar */
    private var calendar: Calendar = Calendar.getInstance()

    /** Bottom Sheet */
    private var datePickerBottomSheet: DatePickerBottomSheet? = null
    private var aiAnalysisBottomSheet: AIAnalysisBottomSheet? = null

    /** Animator */
    private var objectAnimator: ObjectAnimator? = null
    private var progressBarAnimator: AnimatorSet? = null

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

        // viewModel data 초기화
        viewModel.resetData()

        // member data api 호출
        memberViewModel.getMember()
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

            for (place in it.place_diversity) {

                if (place.size >= 3) {
                    viewModel.coordinateToRegionCode(place[1].toString(), place[2].toString())
                }
            }
        }

        /** Region Code List */
        viewModel.regionCodeData.observe(this) { list ->

            updatePlace(list)
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

            // 노출 여부 초기화
            ctNoneData.visibility = View.GONE
            ctAnalysisContents.visibility = View.GONE

            // Header
            incHeader.tvTitle.text = getString(R.string.navigation_analysis)
            incHeader.ivAddChat.visibility = View.GONE

            // Date
            tvDate.text = DateUtil.getDateWithYearMonthDay(calendar)

            // 데이터 없음
            tvNoneData.text = getString(R.string.analysis_text_1)

            incScoreBoard.apply {

                // 점수 title
                incRegularity.tvTitle.text = getString(R.string.analysis_text_24)
                incScreenTime.tvTitle.text = getString(R.string.analysis_text_25)
                incActivity.tvTitle.text = getString(R.string.analysis_text_26)
                incPlace.tvTitle.text = getString(R.string.analysis_text_27)
                incLight.tvTitle.text = getString(R.string.analysis_text_28)
                incDetail.tvTitle.text = getString(R.string.analysis_text_29)
                incDetail.tvResult.text = getString(R.string.analysis_text_31)

                // AI 분석 결과 노출 여부 설정
                incDetail.progressBar.visibility = View.GONE
                incDetail.tvScore.visibility = View.GONE
                incDetail.cvResult.visibility = View.VISIBLE
            }

            // 장소의 다양성
            tvPlaceTitle.text = getString(R.string.analysis_text_32)
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

                    // offset ViewPager
                    setViewPagerDecorator()
                }
            }
        }
    }

    /**
     * Set ViewPager Decorator
     */
    private fun setViewPagerDecorator() {

        if (initSetPreviewBothSide) return

        context?.let {

            with(mBinding.viewPager) {

                setPreviewBothSide(20.toDp(it), 16.toDp(it), 1)

                initSetPreviewBothSide = true
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

            // AI 분석 결과
            incScoreBoard.incDetail.cvResult.setOnClickListener { openAIAnalysisBottomSheet() }
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

        context?.let { ctx ->

            with(mBinding) {

                incScoreBoard.apply {

                    // 생활의 규칙성 점수
                    val regularity = (dao.circadian_rhythm_score * 100).roundToInt()
                    incRegularity.tvScore.text = getString(R.string.unit_analysis_score, regularity)

                    // 핸드폰 사용 시간 점수
                    val screenTime = (dao.phone_usage_score * 100).roundToInt()
                    incScreenTime.tvScore.text = getString(R.string.unit_analysis_score, screenTime)

                    // 활동 점수
                    val activity = (dao.activity_score * 100).roundToInt()
                    incActivity.tvScore.text = getString(R.string.unit_analysis_score, activity)

                    // 장소의 다양성 점수
                    val place = (dao.location_diversity_score * 100).roundToInt()
                    incPlace.tvScore.text = getString(R.string.unit_analysis_score, place)

                    // 빛 노출량 점수
                    val light = (dao.illumination_exposure_score * 100).roundToInt()
                    incLight.tvScore.text = getString(R.string.unit_analysis_score, light)

                    // start animation
                    setProgressBarAnimation(regularity, screenTime, activity, place, light)
                }

                incDescription.apply {

                    // 집에 머무른 시간
                    val home = (dao.home_stay_percentage * 100).roundToInt()
                    tvHomeTitle.text = if (home >= 50) getString(R.string.analysis_text_3) else getString(R.string.analysis_text_4)
                    tvHomeDescription.text = getString(R.string.analysis_text_7, home)

                    MethodStorageUtil.setSpannable(tvHomeDescription, 8, home.toString().length + 9, 16.toDp(ctx).toInt(), R.font.suit_extrabold, ContextCompat.getColor(ctx, R.color.color_3377ff))

                    // 생활의 규칙성
                    val regularity = (dao.life_routine_consistency * 100).roundToInt()
                    tvRegularityTitle.text = if (regularity >= 50) getString(R.string.analysis_text_6) else getString(R.string.analysis_text_5)
                    tvRegularityDescription.text = getString(R.string.analysis_text_8, regularity)

                    MethodStorageUtil.setSpannable(tvRegularityDescription, 3, regularity.toString().length + 4, 16.toDp(ctx).toInt(), R.font.suit_extrabold, ContextCompat.getColor(ctx, R.color.color_3377ff))
                }

                // 장소의 다양성 노출 여부
                incDiversity.root.visibility= dao.place_diversity.isEmpty().toGoneOrVisible()
            }
        }
    }

    /**
     * Update Place
     *
     * @param list
     */
    private fun updatePlace(list: ArrayList<AddressModel>) {

        context?.let { ctx ->

            with(mBinding.incDiversity) {

                val diversityList = viewModel.analysisData.value?.place_diversity ?: ArrayList()
                if (list.size != diversityList.size) return

                list.forEachIndexed { index, place ->

                    if (place.documents.isNotEmpty()) {

                        val percent = diversityList[index][0]
                        val text = "${place.documents.firstOrNull()?.address_name}, ${percent}%"

                        val rootView = when(index) {
                            0 -> ctFirst
                            1 -> ctSecond
                            else -> ctThird
                        }

                        val textView = when(index) {
                            0 -> tvAddressFirst
                            1 -> tvAddressSecond
                            else -> tvAddressThird
                        }

                        textView.text = text
                        rootView.visibility = View.VISIBLE

                        val start = textView.text.length - percent.toString().length - 1
                        MethodStorageUtil.setSpannable(textView, start, start + percent.toString().length + 1, 14.toDp(ctx).toInt(), R.font.suit_bold, ContextCompat.getColor(ctx, R.color.color_3377ff))
                    }
                }
            }
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
        map.labelManager?.layer?.removeAll()

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
     * Open AI Analysis Result BottomSheet
     */
    private fun openAIAnalysisBottomSheet() {

        aiAnalysisBottomSheet?.dismiss()
        aiAnalysisBottomSheet = AIAnalysisBottomSheet(
            memberViewModel.memberData.value?.name,
            viewModel.analysisData.value?.ai_analysis_result
        )
        aiAnalysisBottomSheet?.show(childFragmentManager, "")
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
     * Set Progress Bar Animation
     *
     * @param regularity
     * @param screenTime
     * @param activity
     * @param place
     * @param light
     */
    private fun setProgressBarAnimation(regularity: Int, screenTime: Int, activity: Int, place: Int, light: Int) {

        with(mBinding) {

            incScoreBoard.apply {

                val regularityBar = ValueAnimator.ofInt(0, regularity).apply {
                    addUpdateListener { incRegularity.progressBar.setProgress(it.animatedValue as Int) }
                }

                val screenTimeBar = ValueAnimator.ofInt(0, screenTime).apply {
                    addUpdateListener { incScreenTime.progressBar.setProgress(it.animatedValue as Int) }
                }

                val activityBar = ValueAnimator.ofInt(0, activity).apply {
                    addUpdateListener { incActivity.progressBar.setProgress(it.animatedValue as Int) }
                }

                val placeBar = ValueAnimator.ofInt(0, place).apply {
                    addUpdateListener { incPlace.progressBar.setProgress(it.animatedValue as Int) }
                }

                val lightBar = ValueAnimator.ofInt(0, light).apply {
                    addUpdateListener { incLight.progressBar.setProgress(it.animatedValue as Int) }
                }

                progressBarAnimator?.cancel()
                progressBarAnimator = AnimatorSet().apply {
                    playTogether(regularityBar, screenTimeBar, activityBar, placeBar, lightBar)
                    duration = 500
                    start()
                }
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