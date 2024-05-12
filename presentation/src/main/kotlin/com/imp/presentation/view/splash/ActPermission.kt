package com.imp.presentation.view.splash

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActPermissionBinding
import com.imp.presentation.widget.extension.toGoneOrVisible
import com.imp.presentation.widget.extension.toVisibleOrGone
import com.imp.presentation.widget.utils.PermissionUtil
import com.imp.presentation.widget.utils.PreferencesUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity
 */
@AndroidEntryPoint
class ActPermission : BaseContractActivity<ActPermissionBinding>() {

    /** 권한 요청 */
    private val permissionActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { selectionPermission() }

    /** multi 권한 요청 */
    private val multiPermissionActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { selectionPermission() }

    /** 권한 거부 시 런처 */
    private val permissionDeniedActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { selectionPermission() }

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
    private var requestNotificationPermission = false
    private var requestCameraPermission = false
    private fun selectionPermission() {

        // 알림 권한 요청
        if (!PermissionUtil.checkPermissionNotification(this) && !requestNotificationPermission) {
            requestNotificationPermission = true
            PermissionUtil.requestPermissionNotification(this, permissionActivityResultLauncher, permissionDeniedActivityResultLauncher)
            return
        }

        // 카메라 권한 요청
        if (!PermissionUtil.checkPermissionCamera(this) && !requestCameraPermission) {
            requestCameraPermission = true
            PermissionUtil.requestPermissionCamera(this, permissionActivityResultLauncher, permissionDeniedActivityResultLauncher, false)
            return
        }

        moveToSplash()
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

    /** Permission Request 여부 */
    private var permissionRequest: Boolean = true

    override fun getViewBinding() = ActPermissionBinding.inflate(layoutInflater)

    override fun initData() {

        requestNotificationPermission = false
        requestCameraPermission = false

        permissionRequest = intent.getBooleanExtra("permission_request", true)
    }

    override fun initView() {

        initDisplay()
        setOnClickListener()
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // 노출 여부
            incHeader.ctHeader.visibility = permissionRequest.toGoneOrVisible()
            tvPermissionTitle.visibility = permissionRequest.toVisibleOrGone()
            incStart.tvButton.visibility = permissionRequest.toVisibleOrGone()

            // header
            incHeader.tvTitle.text = getString(R.string.my_page_text_9)
            tvPermissionTitle.text = getString(R.string.permission_text_1)
            tvSubTitle.text = getString(R.string.permission_text_2)

            // 위치 권한
            incLocation.tvTitle.text = getString(R.string.permission_text_3)
            incLocation.tvDescription.text = getString(R.string.permission_text_4)

            // 활동 권한
            incActivity.tvTitle.text = getString(R.string.permission_text_5)
            incActivity.tvDescription.text = getString(R.string.permission_text_6)

            // 전화 권한
            incCall.tvTitle.text = getString(R.string.permission_text_12)
            incCall.tvDescription.text = getString(R.string.permission_text_13)

            // 알림 권한
            incNotification.tvTitle.text = getString(R.string.permission_text_7)
            incNotification.tvDescription.text = getString(R.string.permission_text_8)

            // 카메 권한
            incCamera.tvTitle.text = getString(R.string.permission_text_14)
            incCamera.tvDescription.text = getString(R.string.permission_text_15)

            // 권한 허용 설명
            tvPermissionDesc.text = getString(R.string.permission_text_9)

            // 시작
            incStart.tvButton.text = getString(R.string.permission_text_10)
            incStart.tvButton.isEnabled = true
        }
    }

    /**
     * Set onClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 뒤로 가기
            incHeader.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            // 시작하기
            incStart.tvButton.setOnClickListener {

                // show permission 여부 저장
                PreferencesUtil.setPreferencesBoolean(this@ActPermission, PreferencesUtil.SHOW_PERMISSION_SCREEN, true)

                // selection permission 요청
                selectionPermission()
            }
        }
    }

    /**
     * Move to Splash Screen
     */
    private fun moveToSplash() {

        if (PermissionUtil.checkPermissions(this)) {

            // splash 화면 이동
            Intent(this@ActPermission, ActSplash::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(this)
            }

            finish()

        } else {

            // permission 요청
            requestPermission()
        }
    }
}