package com.imp.presentation.view.member.register.activity

import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActMemberLoginBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Register Activity
 */
@AndroidEntryPoint
class ActRegister : BaseContractActivity<ActMemberLoginBinding>() {

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