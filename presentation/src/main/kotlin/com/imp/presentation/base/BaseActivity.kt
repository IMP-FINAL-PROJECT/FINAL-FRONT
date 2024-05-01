package com.imp.presentation.base

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.imp.data.tracking.constants.BaseConstants
import com.imp.data.tracking.data.SensorDataStore
import com.imp.data.tracking.service.TrackingForegroundService
import com.imp.data.tracking.work.TrackingWorkManager
import com.imp.presentation.R
import com.imp.presentation.view.dialog.CommonPopup
import com.imp.presentation.widget.utils.MethodStorageUtil
import com.imp.presentation.widget.utils.PreferencesUtil
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Base Activity
 */
abstract class BaseActivity<B: ViewBinding> : AppCompatActivity() {

    var _mBinding: B? = null
    protected val mBinding get() = _mBinding!!
    protected lateinit var mDisposable: CompositeDisposable

    var slideAnimation = true
    var verticalSlideAnimation = false
    var fullScreen = false
    var transparentStatusVar = false

    var backPressedCallback: OnBackPressedCallback? = null

    /** Common Popup */
    private var commonPopup: CommonPopup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mBinding = getViewBinding()
        setContentView(mBinding.root)
        initData()
        setFullScreen()
        initView()
        transparentStatusBar()
        whiteStatusAndNavigationBar()

        // Slide 애니메이션 여부
        activityOpenAnimation()

        // tracking service 실행 여부
        checkTrackingServiceIsRunning()

        // 뒤로가기 콜백
        if (backPressedCallback != null) onBackPressedDispatcher.addCallback(this, backPressedCallback!!)
    }

    override fun finish() {
        super.finish()

        activityCloseAnimation()
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
     * Activity Close Animation
     */
    fun activityCloseAnimation() {

        if (slideAnimation) {
            overridePendingTransition(R.anim.anim_window_close_in, R.anim.anim_window_close_out)
        } else if (verticalSlideAnimation) {
            overridePendingTransition(
                R.anim.anim_window_vertical_close_in,
                R.anim.anim_window_vertical_close_out
            )
        } else {
            overridePendingTransition(0, 0)
        }
    }

    override fun setRequestedOrientation(requestedOrientation: Int) {

        try {
            super.setRequestedOrientation(requestedOrientation)
        } catch (exception: Exception){
            exception.printStackTrace()
        }
    }

    /**
     * fullscreen
     */
    private fun setFullScreen() {

        if (!fullScreen) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp

            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        } else {

            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    /**
     * 상태바 투명하게 설정
     */
    private fun transparentStatusBar() {

        if (!transparentStatusVar) return

        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        } else {
            window.setDecorFitsSystemWindows(false)
        }
    }

    /**
     * Status Bar, Navigation Bar White 색상으로 설정
     */
    private fun whiteStatusAndNavigationBar() {

        window.navigationBarColor = ContextCompat.getColor(this@BaseActivity, R.color.white)
        window.statusBarColor = ContextCompat.getColor(this@BaseActivity, R.color.white)

        WindowInsetsControllerCompat(window, mBinding.root).isAppearanceLightNavigationBars = true
        WindowInsetsControllerCompat(window, mBinding.root).isAppearanceLightStatusBars = true
    }

    /**
     * Status Bar 색상 설정
     */
    fun setStatusBarColor(colorId: Int = -1) {

        val color = if (colorId != -1) colorId else R.color.white
        window.statusBarColor = ContextCompat.getColor(this@BaseActivity, color)

        WindowInsetsControllerCompat(window, mBinding.root).isAppearanceLightStatusBars = true
    }

    /**
     * Check Tracking Service is Running
     */
    private fun checkTrackingServiceIsRunning() {

        val isTracking = MethodStorageUtil.isServiceRunning(this)
        PreferencesUtil.setPreferencesBoolean(this, PreferencesUtil.TRACKING_SWITCH_KEY, isTracking)
    }

    /**
     * Show Common Popup
     *
     * @param titleText
     * @param leftText
     * @param rightText
     * @param leftCallback
     * @param rightCallback
     * @param cancelable
     */
    fun showCommonPopup(
        titleText: String,
        leftText: String? = null,
        rightText: String? = null,
        leftCallback: () -> Unit = {},
        rightCallback: () -> Unit = {},
        cancelable: Boolean
    ) {
        commonPopup?.dismiss()
        commonPopup = CommonPopup(this, titleText, leftText, rightText, leftCallback, rightCallback, cancelable)
        commonPopup?.show()
    }

    /**
     * Start Tracking Service
     */
    fun startTrackingService() {

        CoroutineScope(Dispatchers.Main).launch {

            val saveTime = async { SensorDataStore.saveTrackingStartTime(applicationContext, System.currentTimeMillis()) }.await()
            if (saveTime) {

                Intent(applicationContext, TrackingForegroundService::class.java).apply {
                    ContextCompat.startForegroundService(applicationContext, this)
                }

                PreferencesUtil.setPreferencesBoolean(applicationContext, PreferencesUtil.TRACKING_SWITCH_KEY, true)
            }
        }
    }

    /**
     * Stop Tracking Service
     */
    fun stopTrackingService() {

        Intent(applicationContext, TrackingForegroundService::class.java).apply {
            stopService(this)
        }

        PreferencesUtil.setPreferencesBoolean(applicationContext, PreferencesUtil.TRACKING_SWITCH_KEY, false)

        // 현재까지 수집한 데이터 저장
        scheduleSaveDataWork()
    }

    /**
     * Schedule Save Data Work
     */
    private fun scheduleSaveDataWork() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequest.Builder(TrackingWorkManager::class.java)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniqueWork(
                BaseConstants.SAVE_STOP_DATA_WORK,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }
}