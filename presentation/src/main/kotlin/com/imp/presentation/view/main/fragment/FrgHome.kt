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
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgHomeBinding
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.viewmodel.HomeViewModel
import com.imp.presentation.widget.component.CommonMapView
import com.imp.presentation.widget.extension.toDp
import com.imp.presentation.widget.utils.DateUtil
import com.imp.presentation.widget.utils.MethodStorageUtil
import com.imp.presentation.widget.utils.PreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Random

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

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgHomeBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        // set status bar color
        activity?.let { if (it is ActMain) it.setCurrentStatusBarColor(BaseConstants.MAIN_NAV_LABEL_HOME) }

        initObserver()
        initDisplay()
        initScoreBoard()
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
        removeMapView()

        super.onPause()
    }

    override fun onDestroy() {

        progressBarValueAnimator?.cancel()

        super.onDestroy()
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Error Callback */
        viewModel.errorCallback.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { errorMessage ->

                context?.let { Toast.makeText(it, errorMessage, Toast.LENGTH_SHORT).show() }
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

                // 이동 경로 map
                val mapView = CommonMapView(ctx).apply {

                    // 현재 위치로 이동
                    setCurrentLocation(true)
                }
                clMapContainer.addView(mapView)
            }
        }
    }

    /**
     * Initialize Score Board
     */
    private fun initScoreBoard() {

        context?.let { ctx ->

            with(mBinding) {

                val score = Random().nextInt(100)

                /** Score */
                tvScore.text = getString(R.string.unit_happy_score, score)
                tvScore.textSize = 12f
                MethodStorageUtil.setSpannable(
                    textView = tvScore,
                    start = 0,
                    end = tvScore.length() - 1,
                    size = 18.toDp(ctx).toInt()
                )

                /** Score Animation */
                progressBarValueAnimator?.cancel()
                progressBarValueAnimator = ValueAnimator.ofInt(0, score).apply {
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
     * Register UI Update BroadcastReceiver
     */
    private fun registerUIUpdateReceiver() {

        context?.let { if (it is ActMain) it.registerUIBroadcast(uiUpdateReceiver) }
    }

    /**
     * Unregister UI Update BroadcastReceiver
     */
    private fun unregisterUIUpdateReceiver() {

        context?.let { if (it is ActMain) it.unregisterUIBroadcast(uiUpdateReceiver) }
    }

    /**
     * Remove Map View
     *   - 2개 이상의 Kakao MapView를 추가할 수 없음
     */
    private fun removeMapView() {
        mBinding.clMapContainer.removeAllViews()
    }
}