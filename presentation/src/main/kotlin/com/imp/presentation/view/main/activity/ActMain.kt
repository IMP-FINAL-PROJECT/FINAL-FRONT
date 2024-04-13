package com.imp.presentation.view.main.activity

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.ActMainBinding
import com.imp.presentation.tracking.data.SensorDataStore
import com.imp.presentation.tracking.service.TrackingForegroundService
import com.imp.presentation.view.mypage.ActEditProfile
import com.imp.presentation.view.mypage.ActManageAccount
import com.imp.presentation.view.webview.ActCommonWebView
import com.imp.presentation.widget.utils.PermissionUtil
import com.imp.presentation.widget.utils.PreferencesUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity
 */
@AndroidEntryPoint
class ActMain : BaseContractActivity<ActMainBinding>() {

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
                PermissionUtil.requestPermissionActivity(this, activityActivityResultLauncher, permissionDeniedActivityResultLauncher, {})
                return
            }

            // 위치 권한 요청
            if (!PermissionUtil.checkPermissionLocation(this)) {
                PermissionUtil.requestPermissionLocation(this, locationActivityResultLauncher, permissionDeniedActivityResultLauncher, {})
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

    /** Navigation Controller */
    private lateinit var navController: NavController

    override fun getViewBinding() = ActMainBinding.inflate(layoutInflater)

    override fun initData() {

        // todo: 임시
        SensorDataStore.context = this
    }

    override fun initView() {

        init()
    }

    /**
     * Initialize
     */
    private fun init() {

        if (PermissionUtil.checkPermissions(this)) {

            // init
            initNavigationBar()

        } else {

            // permission 요청
            requestPermission()
        }
    }

    /**
     * Initialize Bottom Navigation Bar
     */
    private fun initNavigationBar() {

        with(mBinding) {

            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)
            navController = (navHostFragment as NavHostFragment).findNavController()

            navigationBar.itemIconTintList = null
            navigationBar.setupWithNavController(navController)
        }
    }

    /**
     * Set Status Bar Color
     *
     * @param destination
     */
    fun setCurrentStatusBarColor(destination: String) {

        when(destination) {

            BaseConstants.MAIN_NAV_LABEL_HOME -> setStatusBarColor(R.color.color_e3e6f0)
            else -> setStatusBarColor()
        }
    }

    /**
     * Start Tracking Service
     */
    fun startTrackingService() {

        Intent(this@ActMain, TrackingForegroundService::class.java).apply {
            ContextCompat.startForegroundService(this@ActMain, this)
        }
    }

    /**
     * Stop Tracking Service
     */
    fun stopTrackingService() {

        Intent(this@ActMain, TrackingForegroundService::class.java).apply {
            stopService(this)
        }
    }

    /**
     * Register UI Update Broadcast
     */
    fun registerUIBroadcast(receiver: BroadcastReceiver) {

        val isTracking = PreferencesUtil.getPreferencesBoolean(this, PreferencesUtil.TRACKING_SWITCH_KEY)
        if (isTracking) {

            IntentFilter().apply {

                addAction(BaseConstants.ACTION_TYPE_UPDATE_LOCATION)
                addAction(BaseConstants.ACTION_TYPE_UPDATE_LIGHT_SENSOR)
                addAction(BaseConstants.ACTION_TYPE_UPDATE_STEP_SENSOR)
                registerReceiver(receiver, this, RECEIVER_EXPORTED)
            }
        }
    }

    /**
     * Unregister UI Update Broadcast
     */
    fun unregisterUIBroadcast(receiver: BroadcastReceiver) {

        val isTracking = PreferencesUtil.getPreferencesBoolean(this, PreferencesUtil.TRACKING_SWITCH_KEY)
        if (isTracking) {

            try {
                unregisterReceiver(receiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Move to Log
     */
    fun moveToLog() {

        Intent(this@ActMain, ActLog::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }
    }

    /**
     * Move to Edit Profile
     */
    fun moveToEditProfile() {

        Intent(this@ActMain, ActEditProfile::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }
    }

    /**
     * Move to Manage Account
     */
    fun moveToManageAccount() {

        Intent(this@ActMain, ActManageAccount::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }
    }

    /**
     * Move to Terms
     */
    fun moveToTerms(title: String, url: String) {

        Intent(this@ActMain, ActCommonWebView::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("header", true)
            putExtra("header_title", title)
            putExtra("url", url)
            startActivity(this)
        }
    }
}