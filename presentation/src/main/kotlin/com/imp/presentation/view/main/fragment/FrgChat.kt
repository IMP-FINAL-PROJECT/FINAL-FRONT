package com.imp.presentation.view.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgChatBinding
import com.imp.presentation.view.main.activity.ActMain

/**
 * Main - Chat Fragment
 */
class FrgChat: BaseFragment<FrgChatBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgChatBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        // set status bar color
        activity?.let { if (it is ActMain) it.setCurrentStatusBarColor(BaseConstants.MAIN_NAV_LABEL_CHAT) }

        initDisplay()
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // Header
            incHeader.tvTitle.text = getString(R.string.navigation_chat)
            incHeader.ivAddChat.visibility = View.VISIBLE
        }
    }
}