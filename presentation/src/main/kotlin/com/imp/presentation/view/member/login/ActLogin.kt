package com.imp.presentation.view.member.login

import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActMemberLoginBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Login Activity
 */
@AndroidEntryPoint
class ActLogin : BaseContractActivity<ActMemberLoginBinding>() {

    override fun getViewBinding() = ActMemberLoginBinding.inflate(layoutInflater)

    override fun initData() {

    }

    override fun initView() {

        initDisplay()
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

    }
}