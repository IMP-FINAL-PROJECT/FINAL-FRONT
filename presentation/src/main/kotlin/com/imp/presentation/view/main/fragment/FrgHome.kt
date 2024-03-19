package com.imp.presentation.view.main.fragment

import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
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
import com.imp.presentation.widget.extension.toDp
import com.imp.presentation.widget.utils.DateUtil
import com.imp.presentation.widget.utils.MethodStorageUtil
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
                        }

                        BaseConstants.ACTION_TYPE_UPDATE_STEP_SENSOR -> {

                            val step = intent.getIntExtra(BaseConstants.INTENT_KEY_STEP_SENSOR, 0)
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

    override fun onDestroy() {

        progressBarValueAnimator?.cancel()
        super.onDestroy()
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Location Map Point List */
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

        with(mBinding) {

            /** Score */
            tvScoreDate.text = DateUtil.getCurrentDateWithText("MM월 dd일")
            tvScoreTitle.text = getString(R.string.home_text_1)
            tvScore.text = getString(R.string.unit_happy_score)

            /** Tracking */
            tvTrackingTitle.text = getString(R.string.home_text_2)
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
     * Register UI Update BroadcastReceiver
     */
    private fun registerUIUpdateReceiver() {

        IntentFilter().apply {

            addAction(BaseConstants.ACTION_TYPE_UPDATE_LOCATION)
            addAction(BaseConstants.ACTION_TYPE_UPDATE_LIGHT_SENSOR)
            addAction(BaseConstants.ACTION_TYPE_UPDATE_STEP_SENSOR)
            context?.registerReceiver(uiUpdateReceiver, this, RECEIVER_EXPORTED)
        }
    }

    /**
     * Unregister UI Update BroadcastReceiver
     */
    private fun unregisterUIUpdateReceiver() {

        try {
            context?.unregisterReceiver(uiUpdateReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}