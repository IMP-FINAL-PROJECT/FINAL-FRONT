package com.imp.presentation.view.mypage

import android.content.Intent
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActMypageManageAccountBinding
import com.imp.presentation.view.splash.ActSplash
import com.imp.presentation.widget.utils.PreferencesUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * Manage Account Activity
 */
@AndroidEntryPoint
class ActManageAccount : BaseContractActivity<ActMypageManageAccountBinding>() {

    override fun getViewBinding() = ActMypageManageAccountBinding.inflate(layoutInflater)

    override fun initData() {

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

            // Header
            incHeader.tvTitle.text = getString(R.string.my_page_text_4)

            // 실시간 트래킹
            tvTrackingTitle.text = getString(R.string.my_page_text_1)
            trackingSwitch.isChecked = PreferencesUtil.getPreferencesBoolean(this@ActManageAccount, PreferencesUtil.TRACKING_SWITCH_KEY)
            trackingSwitch.setOnCheckedChangeListener { _, isChecked ->

                if (isChecked) {
                    startTrackingService()
                } else {
                    stopTrackingService()

                    // todo: 임시
                    PreferencesUtil.setPreferencesInt(this@ActManageAccount, PreferencesUtil.TRACKING_SCREEN_AWAKE_KEY, 0)
                }

                PreferencesUtil.setPreferencesBoolean(this@ActManageAccount, PreferencesUtil.TRACKING_SWITCH_KEY, isChecked)
            }

            // 로그아웃
            incLogout.tvTitle.text = getString(R.string.my_page_text_11)
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 뒤로 가기
            incHeader.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            // 로그아웃
            incLogout.llContents.setOnClickListener {

                // 저장되어 있는 계정 정보 제거
                PreferencesUtil.deletePreferences(this@ActManageAccount, PreferencesUtil.AUTO_LOGIN_ID_KEY)
                PreferencesUtil.deletePreferences(this@ActManageAccount, PreferencesUtil.AUTO_LOGIN_PASSWORD_KEY)

                // 시작 화면으로 이동
                Intent(this@ActManageAccount, ActSplash::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(this)
                    finishAffinity()
                }
            }
        }
    }
}