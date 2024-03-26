package com.imp.presentation.view.mypage

import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActMypageEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Edit Profile Activity
 */
@AndroidEntryPoint
class ActEditProfile : BaseContractActivity<ActMypageEditProfileBinding>() {

    override fun getViewBinding() = ActMypageEditProfileBinding.inflate(layoutInflater)

    override fun initData() {

    }

    override fun initView() {

    }
}