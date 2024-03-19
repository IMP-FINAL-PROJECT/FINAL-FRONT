package com.imp.presentation.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.imp.presentation.R
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * Base Splash Activity
 */
@SuppressLint("CustomSplashScreen")
abstract class BaseSplashActivity<B: ViewBinding> : AppCompatActivity() {

    private var _mBinding: B? = null
    val mBinding get() = _mBinding!!
    protected lateinit var mDisposable: CompositeDisposable

    var slideAnimation = false
    var verticalSlideAnimation = false

    var bgStatusBar: Int? = null
    var lightStatusBar = true
    var lightNavigationBar = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        initStatusBarAndNaviBar()

        _mBinding = getViewBinding()
        setContentView(mBinding.root)
        initData()
        initView()

        // Slide 애니메이션 여부
        activityOpenAnimation()
    }

    override fun setContentView(layoutResID: Int) {
        layoutInflater.inflate(layoutResID, null).let {
            super.setContentView(it)
        }
    }

    abstract fun initData()
    abstract fun initView()
    abstract fun getViewBinding(): B

    /**
     * Activity Open Animation
     */
    private fun activityOpenAnimation() {

        if (slideAnimation) {
            overridePendingTransition(R.anim.anim_window_in, R.anim.anim_window_out)
        } else if (verticalSlideAnimation) {
            overridePendingTransition(
                R.anim.anim_window_vertical_in,
                R.anim.anim_window_vertical_out
            )
        } else {
            overridePendingTransition(0, 0)
        }
    }

    /**
     * Status bar and navigation bar setting
     */
    private fun initStatusBarAndNaviBar() {

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // Status 바 배경색 설정
        bgStatusBar?.let {

            window.statusBarColor = ContextCompat.getColor(this@BaseSplashActivity, it)

        } ?: let {

            val color = if (lightStatusBar) Color.WHITE else Color.BLACK
            if (window.statusBarColor != color) window.statusBarColor = color
        }

        // Status bar 텍스트 색상 설정
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = lightStatusBar

        // 네비게이션 바 아이콘 색상 설정
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = !lightNavigationBar
    }
}