package com.imp.presentation.view.main.fragment

import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.imp.domain.model.HomeModel
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgHomeBinding
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.viewmodel.HomeViewModel
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
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.TrackingManager
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main - Home Fragment
 */
class FrgHome: BaseFragment<FrgHomeBinding>() {

    /** Home ViewModel */
    private val viewModel: HomeViewModel by activityViewModels()

    /** Animator */
    private var progressBarValueAnimator: ValueAnimator? = null

    /** Coroutine */
    private val coroutineMain = CoroutineScope(Dispatchers.Main)

    /** UI Update Broadcast Receiver */
    private val uiUpdateReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent == null) return

            with(mBinding) {

                coroutineMain.launch {

                    when(intent.action) {

                        BaseConstants.ACTION_TYPE_UPDATE_LOCATION -> {

                            val location = intent.getParcelableExtra<Location>(BaseConstants.INTENT_KEY_LOCATION)
                            if (location != null) {

                                addPoint(LatLng.from(location.latitude, location.longitude))
                            }
                        }

                        BaseConstants.ACTION_TYPE_UPDATE_LIGHT_SENSOR -> {

                            val light = intent.getFloatExtra(BaseConstants.INTENT_KEY_LIGHT_SENSOR, 0f)

                            tvLight.text = getString(R.string.unit_light, "실내", light.toInt())
                        }

                        BaseConstants.ACTION_TYPE_UPDATE_STEP_SENSOR -> {

                            val step = intent.getIntExtra(BaseConstants.INTENT_KEY_STEP_SENSOR, 0)

                            tvStep.text = getString(R.string.unit_steps, step.toString())
                        }
                    }
                }
            }
        }
    }

    /** 지도 관련 변수 */
    private var kakaoMap: KakaoMap? = null
    private var centerLabel: Label? = null
    private var tackingManager: TrackingManager? = null

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgHomeBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        // set status bar color
        activity?.let { if (it is ActMain) it.setCurrentStatusBarColor(BaseConstants.MAIN_NAV_LABEL_HOME) }

        initObserver()
        initDisplay()
        initMapView()
        initSeekbar()
        setOnClickListener()

        // log data api 호출
        context?.let { ctx ->

            val id = PreferencesUtil.getPreferencesString(ctx, PreferencesUtil.AUTO_LOGIN_ID_KEY)
            viewModel.homeData(id)
        }
    }

    override fun onResume() {
        super.onResume()

        // Register UI Update Receiver
        registerUIUpdateReceiver()
        updateUI()
    }

    override fun onPause() {

        // Unregister UI Update Receiver
        unregisterUIUpdateReceiver()

        super.onPause()
    }

    override fun onDestroy() {

        progressBarValueAnimator?.cancel()
        resetMapView()

        super.onDestroy()
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Home Data */
        viewModel.homeData.observe(this) { initScoreBoard(it) }

        /** Error Callback */
        viewModel.errorCallback.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { error ->

                context?.let { Toast.makeText(it, error.message, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        context?.let { ctx ->

            with(mBinding) {

                /** Score */
                tvScoreDate.text = DateUtil.getCurrentDateWithText("MM월 dd일")
                tvScoreTitle.text = getString(R.string.home_text_1)
                tvScore.text = getString(R.string.unit_happy_score)

                /** Mood */
                tvMood.text = getString(R.string.home_text_5)
                tvSave.text = getString(R.string.save)

                /** Tracking */
                tvTrackingTitle.text = getString(R.string.home_text_2)

                tvScreenTimeTitle.text = getString(R.string.log_text_1)
                tvScreenTime.text = getString(R.string.unit_hour_minute, 0, 0)

                tvScreenAwakeTitle.text = getString(R.string.log_text_2)
                tvScreenAwake.text = getString(R.string.unit_count, 0)

                tvStepTitle.text = getString(R.string.log_text_3)
                tvStep.text = getString(R.string.unit_steps, 0.toString())

                tvLightTitle.text = getString(R.string.home_text_3)
                tvLight.text = getString(R.string.unit_light, "", 0)

                tvMapTitle.text = getString(R.string.log_text_5)

                tvTrackingOff.text = getString(R.string.home_text_4)
            }
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        context?.let { ctx ->

            with(mBinding) {

                // 트래킹 상세
                llTrackingTitle.setOnClickListener { if (ctx is ActMain) ctx.moveToLog() }

                // 기분 저장
                tvSave.setOnClickListener {

                    val id = PreferencesUtil.getPreferencesString(ctx, PreferencesUtil.AUTO_LOGIN_ID_KEY)
                    viewModel.saveMood(id, indicatorSeekbar.progress)
                }
            }
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
                }
            })
        }
    }

    /**
     * Initialize Score Board
     */
    private fun initScoreBoard(data: HomeModel) {

        context?.let { ctx ->

            with(mBinding) {

                /** Score */
                tvScore.text = getString(R.string.unit_happy_score, data.point)
                tvScore.textSize = 12f
                MethodStorageUtil.setSpannable(
                    textView = tvScore,
                    start = 0,
                    end = tvScore.length() - 1,
                    size = 18.toDp(ctx).toInt()
                )

                /** Score Animation */
                progressBarValueAnimator?.cancel()
                progressBarValueAnimator = ValueAnimator.ofInt(0, data.point).apply {
                    addUpdateListener {

                        val value = it.animatedValue as Int
                        progressBar.setProgress(value)
                    }
                    duration = 500
                    start()
                }
            }
        }
    }

    /**
     * Initialize SeekBar
     */
    private fun initSeekbar() {

        with(mBinding.indicatorSeekbar) {

            onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {

                    val mood = when(progress) {
                        in 0 until 20 -> String(Character.toChars(0x1F62D))
                        in 20 until 40 -> String(Character.toChars(0x1F622))
                        in 40 until 60 -> String(Character.toChars(0x1F610))
                        in 60 until 80 -> String(Character.toChars(0x1F60A))
                        else -> String(Character.toChars(0x1F606))
                    }
                    setIndicatorTextFormat("$mood \${PROGRESS}")
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {}
            }
        }
    }

    /**
     * 실시간 트래킹 화면 노출 여부
     */
    private fun controlTrackingUI() {

        context?.let { ctx ->

            with(mBinding) {

                val isTracking = PreferencesUtil.getPreferencesBoolean(ctx, PreferencesUtil.TRACKING_SWITCH_KEY)
                ctTrackingOff.visibility = isTracking.toGoneOrVisible()
                llTrackingCard.visibility = isTracking.toVisibleOrGone()
            }
        }
    }

    /**
     * Update UI
     */
    private fun updateUI() {

        context?.let { ctx ->

            with(mBinding) {

                // 화면 깨우기
                val awakeCount = PreferencesUtil.getPreferencesInt(ctx, PreferencesUtil.TRACKING_SCREEN_AWAKE_KEY)
                tvScreenAwake.text = getString(R.string.unit_count, awakeCount)
            }
        }
    }

    /**
     * Add Center Label
     *
     * @param point
     */
    private fun addPoint(point: LatLng) {

        if (centerLabel == null) {

            kakaoMap?.labelManager?.layer?.let { layer ->

                layer.removeAll()
                centerLabel = layer.addLabel(LabelOptions.from("centerLabel", point).setStyles(R.drawable.icon_map_marker))
            }

        } else {

            centerLabel?.moveTo(point)
        }

        if (tackingManager == null) {

            tackingManager = kakaoMap?.trackingManager
            tackingManager?.startTracking(centerLabel)
            tackingManager?.setTrackingRotation(false)
        }
    }

    /**
     * Reset MapView
     */
    private fun resetMapView() {

        kakaoMap?.labelManager?.clearAll()
        centerLabel?.remove()

        kakaoMap = null
        centerLabel = null
        tackingManager = null
    }

    /**
     * Register UI Update BroadcastReceiver
     */
    private fun registerUIUpdateReceiver() {

        context?.let { if (it is ActMain) it.registerUIBroadcast(uiUpdateReceiver) }

        controlTrackingUI()
    }

    /**
     * Unregister UI Update BroadcastReceiver
     */
    private fun unregisterUIUpdateReceiver() {

        context?.let { if (it is ActMain) it.unregisterUIBroadcast(uiUpdateReceiver) }

        controlTrackingUI()
    }
}