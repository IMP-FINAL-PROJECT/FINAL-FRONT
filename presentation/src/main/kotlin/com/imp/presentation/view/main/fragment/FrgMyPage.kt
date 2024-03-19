package com.imp.presentation.view.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgMypageBinding
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.widget.utils.PreferencesUtil

/**
 * Main - MyPage Fragment
 */
class FrgMyPage: BaseFragment<FrgMypageBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgMypageBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        // set status bar color
        activity?.let { if (it is ActMain) it.setCurrentStatusBarColor(BaseConstants.MAIN_NAV_LABEL_MYPAGE) }

        initDisplay()
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        context?.let { ctx ->

            with(mBinding) {

                // Header
                incHeader.tvTitle.text = getString(R.string.navigation_mypage)
                incHeader.ivAddChat.visibility = View.GONE

                // todo: 임시 트래킹 on/off 버튼
                tvTrackingTitle.text = getString(R.string.my_page_text_1)

                trackingSwitch.isChecked = PreferencesUtil.getPreferencesBoolean(ctx, PreferencesUtil.TRACKING_SWITCH_KEY)
                trackingSwitch.setOnCheckedChangeListener { _, isChecked ->

                    if (ctx is ActMain) {

                        if (isChecked) {
                            ctx.startTrackingService()
                        } else {
                            ctx.stopTrackingService()

                            // todo: 임시
                            PreferencesUtil.setPreferencesInt(ctx, PreferencesUtil.TRACKING_SCREEN_AWAKE_KEY, 0)
                        }
                    }

                    PreferencesUtil.setPreferencesBoolean(ctx, PreferencesUtil.TRACKING_SWITCH_KEY, isChecked)
                }
            }
        }
    }
}