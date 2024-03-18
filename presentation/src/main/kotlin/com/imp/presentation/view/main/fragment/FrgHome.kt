package com.imp.presentation.view.main.fragment

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.databinding.FrgHomeBinding
import com.imp.presentation.widget.extension.toDp
import com.imp.presentation.widget.utils.DateUtil
import com.imp.presentation.widget.utils.MethodStorageUtil
import java.util.Random

/**
 * Main - Home Fragment
 */
class FrgHome: BaseFragment<FrgHomeBinding>() {

    /** Animator */
    private var progressBarValueAnimator: ValueAnimator? = null

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgHomeBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        initDisplay()
        initScoreBoard()
    }

    override fun onDestroy() {

        progressBarValueAnimator?.cancel()
        super.onDestroy()
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            /** App Title */
            tvAppTitle.text = getString(R.string.app_name)

            /** Date */
            tvScoreDate.text = DateUtil.getCurrentDateWithText("MM월 dd일")

            /** Score Title */
            tvScoreTitle.text = getString(R.string.home_text_1)

            /** Score */
            tvScore.text = getString(R.string.unit_happy_score)

            /** Tracking Title */
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
}