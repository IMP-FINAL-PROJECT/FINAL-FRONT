package com.imp.presentation.view.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgMypageBinding
import com.imp.presentation.view.main.activity.ActMain

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
    }
}