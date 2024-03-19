package com.imp.presentation.view.splash

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import com.imp.presentation.base.BaseSplashActivity
import com.imp.presentation.databinding.ActSplashBinding
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.widget.extension.toVisibleOrGone
import com.imp.presentation.widget.utils.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity
 */
@AndroidEntryPoint
class ActSplash : BaseSplashActivity<ActSplashBinding>() {

    /** 권한 요청 */
    private val notificationActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {

        if (PermissionUtil.checkPermissionNotification(this)) {
            init()
        } else {
            finish()
        }
    }

    /** 권한 요청 */
    private val locationActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

        if (PermissionUtil.checkPermissionLocation(this)) {
            init()
        } else {
            finish()
        }
    }

    /** 권한 요청 */
    private val backgroundLocationActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {

        if (PermissionUtil.checkPermissionBackgroundLocation(this)) {
            init()
        } else {
            finish()
        }
    }

    /** 권한 요청 */
    private val activityActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {

        if (PermissionUtil.checkPermissionActivity(this)) {
            init()
        } else {
            finish()
        }
    }

    /** 권한 거부 시 런처 */
    private val permissionDeniedActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        if (PermissionUtil.checkPermissions(this)) {
            init()
        } else {
            finish()
        }
    }

    /**
     * Request Permission
     */
    private fun requestPermission() {

        if (!PermissionUtil.checkPermissions(this)) {

            // 알림 권한 요청
            if (!PermissionUtil.checkPermissionNotification(this)) {
                PermissionUtil.requestPermissionNotification(this, notificationActivityResultLauncher, permissionDeniedActivityResultLauncher)
                return
            }

            // 활동 권한 요청
            if (!PermissionUtil.checkPermissionActivity(this)) {
                PermissionUtil.requestPermissionActivity(this, activityActivityResultLauncher, permissionDeniedActivityResultLauncher)
                return
            }

            // 위치 권한 요청
            if (!PermissionUtil.checkPermissionLocation(this)) {
                PermissionUtil.requestPermissionLocation(this, locationActivityResultLauncher, permissionDeniedActivityResultLauncher)
                return
            }

            // 백그라운드 위치 권한 요청
            if (!PermissionUtil.checkPermissionBackgroundLocation(this)) {

                // todo 항상 허용 요청 팝업
                PermissionUtil.requestPermissionBackgroundLocation(this, backgroundLocationActivityResultLauncher, permissionDeniedActivityResultLauncher)
                return
            }
        }
    }

    override fun getViewBinding() = ActSplashBinding.inflate(layoutInflater)

    override fun initData() {

    }

    override fun initView() {

        init()
    }

    /**
     * Initialize
     */
    private fun init() {

        if (PermissionUtil.checkPermissions(this)) {

            loadingLottieControl(true)

            // init
            Handler(Looper.getMainLooper()).postDelayed({

                loadingLottieControl(false)
                moveToNext()

            }, 1000)

        } else {

            // permission 요청
            requestPermission()
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
     * Move to Next Screen
     */
    private fun moveToNext() {

        // 메인 화면으로 이동
        Intent(this@ActSplash, ActMain::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }

        finish()
    }
}