package com.imp.presentation.view.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgAnalysisBinding
import com.imp.presentation.view.main.activity.ActMain


/**
 * Main - Analysis Fragment
 */
class FrgAnalysis: BaseFragment<FrgAnalysisBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgAnalysisBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        // set status bar color
        activity?.let { if (it is ActMain) it.setCurrentStatusBarColor(BaseConstants.MAIN_NAV_LABEL_LOG) }

        initObserver()
        initDisplay()
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // Header
            incHeader.tvTitle.text = getString(R.string.navigation_analysis)
            incHeader.ivAddChat.visibility = View.GONE
        }
    }
}