package com.imp.presentation.view.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.animation.doOnStart
import com.imp.presentation.R
import com.imp.presentation.base.BaseSplashActivity
import com.imp.presentation.databinding.ActSplashBinding
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.view.member.login.ActLogin
import com.imp.presentation.view.member.register.activity.ActRegister
import com.imp.presentation.viewmodel.MemberViewModel
import com.imp.presentation.widget.extension.toVisibleOrGone
import com.imp.presentation.widget.utils.PreferencesUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity
 */
@AndroidEntryPoint
class ActSplash : BaseSplashActivity<ActSplashBinding>() {

    /** Member ViewModel */
    private val viewModel: MemberViewModel by viewModels()

    // splash main animator
    private var animatorSet: AnimatorSet? = null

    override fun getViewBinding() = ActSplashBinding.inflate(layoutInflater)

    override fun initData() {

    }

    override fun initView() {

        initObserver()
        initDisplay()
        setOnClickListener()
    }

    override fun onDestroy() {
        animatorSet?.cancel()
        super.onDestroy()
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Login Data */
        viewModel.loginData.observe(this) { _ ->

            // 메인 화면 이동
            moveToMain()
        }

        /** Error Callback */
        viewModel.errorCallback.observe(this) { event ->
            event.getContentIfNotHandled()?.let { error ->

                // splash main 화면 노출 전일 경우, 초기화
                if (mBinding.ctMainButton.visibility == View.GONE) {

                    initMainScreen()
                }

                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        loadingLottieControl(true)

        // init
        Handler(Looper.getMainLooper()).postDelayed({

            loadingLottieControl(false)
            checkAutoLogin()

        }, 1000)
    }

    /**
     * Set onClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 로그인 화면 이동
            incLogin.tvButton.setOnClickListener { moveToLogin() }

            // 회원 가입 화면 이동
            tvRegister.setOnClickListener { moveToRegister() }
        }
    }

    /**
     * Check Auto Login
     */
    private fun checkAutoLogin() {

        val userId = PreferencesUtil.getPreferencesString(this, PreferencesUtil.AUTO_LOGIN_ID_KEY)
        val userPassword = PreferencesUtil.getPreferencesString(this, PreferencesUtil.AUTO_LOGIN_PASSWORD_KEY)

        if (userId.isNotEmpty() && userPassword.isNotEmpty()) {

            // login api 요청
            viewModel.login(userId, userPassword)

        } else {

            // splash main 화면 초기화
            initMainScreen()
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