package com.imp.presentation.view.mypage

import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.ActTermsBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Term Activity
 */
@AndroidEntryPoint
class ActTerms : BaseContractActivity<ActTermsBinding>() {

    /** Terms Type */
    private var type: String = ""

    override fun getViewBinding() = ActTermsBinding.inflate(layoutInflater)

    override fun initData() {

        type = intent?.getStringExtra("type") ?: ""
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
            incHeader.tvTitle.text = when(type) {

                BaseConstants.TERMS_TYPE_USAGE -> getString(R.string.my_page_text_6)
                BaseConstants.TERMS_TYPE_PRIVACY -> getString(R.string.my_page_text_7)
                BaseConstants.TERMS_TYPE_SENSITIVE -> getString(R.string.register_text_6)
                BaseConstants.TERMS_TYPE_OPTIONAL_SENSITIVE -> getString(R.string.register_text_7)
                else -> ""
            }

            // terms
            tvTerms.text = when(type) {

                BaseConstants.TERMS_TYPE_USAGE -> getString(R.string.my_page_text_12)
                BaseConstants.TERMS_TYPE_PRIVACY -> getString(R.string.my_page_text_13)
                BaseConstants.TERMS_TYPE_SENSITIVE -> getString(R.string.my_page_text_14)
                BaseConstants.TERMS_TYPE_OPTIONAL_SENSITIVE -> getString(R.string.my_page_text_14)
                else -> ""
            }
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 뒤로 가기
            incHeader.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }
}