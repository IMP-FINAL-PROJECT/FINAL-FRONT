package com.imp.presentation.view.main.activity

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.ActMainBinding
import com.imp.presentation.view.mypage.ActEditProfile
import com.imp.presentation.view.mypage.ActManageAccount
import com.imp.presentation.view.splash.ActPermission
import com.imp.presentation.view.webview.ActCommonWebView
import com.imp.presentation.widget.utils.MethodStorageUtil
import com.imp.presentation.widget.utils.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity
 */
@AndroidEntryPoint
class ActMain : BaseContractActivity<ActMainBinding>() {

    /** 권한 요청 */
    private val permissionActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { init() }

    /** multi 권한 요청 */
    private val multiPermissionActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { init() }

    /** 권한 거부 시 런처 */
    private val permissionDeniedActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { init() }

    /**
     * Show Permission Denied Popup
     */
    private val permissionDeniedPopup: (() -> Unit) -> Unit = { deniedCallback ->

        showCommonPopup(
            titleText = getString(R.string.popup_text_1),
            rightText = getString(R.string.permission_text_11),
            rightCallback = { deniedCallback.invoke() },
            cancelable = false
        )
    }

    /**
     * Request Selection Permission
     */
    private var checkSelectionPermission = false
    private fun selectionPermission() {

        // 알림 권한 요청
        if (!PermissionUtil.checkPermissionNotification(this) && !checkSelectionPermission) {
            checkSelectionPermission = true
            PermissionUtil.requestPermissionNotification(this, permissionActivityResultLauncher, permissionDeniedActivityResultLauncher) { init() }
            return

        } else {

            // 필수 권한 요청
            requestPermission()
            return
        }
    }

    /**
     * Request Permission
     */
    private fun requestPermission() {

        if (!PermissionUtil.checkPermissions(this)) {

            // 활동 권한 요청
            if (!PermissionUtil.checkPermissionActivity(this)) {
                PermissionUtil.requestPermissionActivity(this, permissionActivityResultLauncher, permissionDeniedActivityResultLauncher, permissionDeniedPopup)
                return
            }

            // 전화 권한 요청
            if (!PermissionUtil.checkPermissionCall(this)) {
                PermissionUtil.requestPermissionCall(this, permissionActivityResultLauncher, permissionDeniedActivityResultLauncher, permissionDeniedPopup)
                return
            }

            // 위치 권한 요청
            if (!PermissionUtil.checkPermissionLocation(this)) {
                PermissionUtil.requestPermissionLocation(this, multiPermissionActivityResultLauncher, permissionDeniedActivityResultLauncher, permissionDeniedPopup)
                return
            }

            // background 위치 권한 요청
            if (!PermissionUtil.checkPermissionBackgroundLocation(this)) {

                // 항상 허용으로 변경 요청
                showCommonPopup(
                    titleText = getString(R.string.popup_text_2),
                    rightText = getString(R.string.permission_text_11),
                    rightCallback = { PermissionUtil.requestPermissionBackgroundLocation(this, permissionActivityResultLauncher, permissionDeniedActivityResultLauncher) },
                    cancelable = false
                )
                return
            }
        }
    }

    /** Navigation Controller */
    private lateinit var navController: NavController

    override fun getViewBinding() = ActMainBinding.inflate(layoutInflater)

    override fun initData() {

    }

    override fun initView() {

        init()
        initNavigationBar()
    }

    /**
     * Initialize
     */
    private fun init() {

        if (!PermissionUtil.checkPermissions(this)) {

            // selection permission 요청
            selectionPermission()
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
     * Register UI Update Broadcast
     */
    fun registerUIBroadcast(receiver: BroadcastReceiver) {

        if (MethodStorageUtil.isServiceRunning(this)) {

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

        if (MethodStorageUtil.isServiceRunning(this)) {

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
     * Move to Score Detail
     */
    fun moveToScoreDetail() {

        Intent(this@ActMain, ActScoreDetail::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }
    }

    /**
     * Move to Chatting
     */
    fun moveToChatting(title: String, url: String, number: String) {

        Intent(this@ActMain, ActCommonWebView::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("header", true)
            putExtra("header_title", title)
            putExtra("url", url)
            putExtra("chatNumber", number)
            putExtra("chat", true)
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

    /**
     * Move to Permission Terms
     */
    fun moveToPermission() {

        Intent(this@ActMain, ActPermission::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("permission_request", false)
            startActivity(this)
        }
    }
}