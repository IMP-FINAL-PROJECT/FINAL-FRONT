package com.imp.presentation.view.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.animation.doOnStart
import com.imp.presentation.R
import com.imp.presentation.base.BaseSplashActivity
import com.imp.presentation.databinding.ActSplashBinding
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.view.member.login.ActLogin
import com.imp.presentation.view.member.register.activity.ActRegister
import com.imp.presentation.widget.extension.toVisibleOrGone
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity
 */
@AndroidEntryPoint
class ActSplash : BaseSplashActivity<ActSplashBinding>() {

    private var animatorSet: AnimatorSet? = null

    override fun getViewBinding() = ActSplashBinding.inflate(layoutInflater)

    override fun initData() {

    }

    override fun initView() {

        initDisplay()
        setClickListener()
    }

    override fun onDestroy() {
        animatorSet?.cancel()
        super.onDestroy()
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        loadingLottieControl(true)

        // init
        Handler(Looper.getMainLooper()).postDelayed({

            loadingLottieControl(false)
            initMainScreen()

        }, 1000)
    }

    /**
     * Set onClickListener
     */
    private fun setClickListener() {

        with(mBinding) {

            // 로그인 화면 이동
            incLogin.tvButton.setOnClickListener { moveToMain() }

            // 회원 가입 화면 이동
            tvRegister.setOnClickListener { moveToMain() }
        }
    }

    /**
     * Initialize Splash Main Screen
     */
    private fun initMainScreen() {

        with(mBinding) {

            tvSubTitle.text = getString(R.string.splash_text_1)
            incLogin.tvButton.text = getString(R.string.login_text_1)
            tvRegister.text = getString(R.string.register_text_1)

            animatorSet?.cancel()
            animatorSet = AnimatorSet().apply {

                doOnStart {

                    tvSubTitle.alpha = 0f
                    ctMainButton.alpha = 0f

                    tvSubTitle.visibility = View.VISIBLE
                    ctMainButton.visibility = View.VISIBLE
                    lottieLoading.visibility = View.GONE
                }
                playTogether(
                    ObjectAnimator.ofFloat(ctTitle, View.TRANSLATION_Y, 0f, -300f),
                    ObjectAnimator.ofFloat(ctMainButton, View.TRANSLATION_Y, 100f, 0f),
                    ObjectAnimator.ofFloat(ctMainButton, View.ALPHA, 0f, 1f),
                    ObjectAnimator.ofFloat(tvSubTitle, View.ALPHA, 0f, 1f)
                )
                duration = 500
            }
            animatorSet?.start()
        }
    }

    /**
     * loading lottie control
     *
     * @param start true -> start / false -> stop
     */
    private fun loadingLottieControl(start: Boolean) {

        with(mBinding.lottieLoading) {

            visibility = start.toVisibleOrGone()
            if (start) { playAnimation() } else { pauseAnimation() }
        }
    }

    /**
     * Move to Main Screen
     */
    private fun moveToMain() {

        // 메인 화면 이동
        Intent(this@ActSplash, ActMain::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }

        finish()
    }

    /**
     * Move to Login Screen
     */
    private fun moveToLogin() {

        // 로그인 화면 이동
        Intent(this@ActSplash, ActLogin::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }
    }

    /**
     * Move to Register Screen
     */
    private fun moveToRegister() {

        // 회원 가입 화면 이동
        Intent(this@ActSplash, ActRegister::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }
    }
}