package com.imp.presentation.view.member.register.activity

import android.content.Intent
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActMemberRegisterBinding
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.widget.extension.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

/**
 * Register Activity
 */
@AndroidEntryPoint
class ActRegister : BaseContractActivity<ActMemberRegisterBinding>() {

    override fun getViewBinding() = ActMemberRegisterBinding.inflate(layoutInflater)

    override fun initData() {

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                hideKeyboard(this@ActRegister, currentFocus)
                activityCloseAnimation()
                finish()
            }
        }
    }

    override fun initView() {

        initDisplay()
        initViewPager()
        setOnClickListener()
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            incHeader.tvTitle.visibility = View.GONE

            tvRegisterTitle.text = getString(R.string.register_text_1)

            // 로그인 버튼
            incNextAndRegister.apply {

                tvButton.text = getString(R.string.next)
                tvButton.isEnabled = false
            }
        }
    }

    /**
     * Initialize ViewPager
     */
    private fun initViewPager() {

        with(mBinding) {

        }
    }

    /**
     * Set onClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

        }
    }

    /**
     * Move to Main
     */
    private fun moveToMain() {

        // 메인 화면 이동
        Intent(this@ActRegister, ActMain::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }

        finishAffinity()
    }
}