package com.imp.presentation.view.main.activity

import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActMainBinding
import com.imp.presentation.widget.utils.PermissionUtil
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

    override fun getViewBinding() = ActMainBinding.inflate(layoutInflater)

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
            val navController = (navHostFragment as NavHostFragment).findNavController()

            navigationBar.itemIconTintList = null
            navigationBar.setupWithNavController(navController)
        }
    }
}