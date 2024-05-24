package com.imp.presentation.view.member.register.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgRegisterTermsBinding
import com.imp.presentation.databinding.IncRegisterTermsBinding
import com.imp.presentation.view.member.register.activity.ActRegister

/**
 * Register - Terms Fragment
 */
class FrgTerms: BaseFragment<FrgRegisterTermsBinding>() {

    companion object {

        fun newInstance(): FrgTerms {
            return FrgTerms().apply {
                arguments = Bundle().apply {}
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgRegisterTermsBinding.inflate(inflater, container, false)

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

            // 설명
            tvDescription.text = getString(R.string.register_text_3)

            // 약관
            initTerms(incAll, R.string.register_text_4)
            initTerms(incUsage, R.string.my_page_text_6)
            initTerms(incPrivacy, R.string.register_text_5)
            initTerms(incSensitive, R.string.register_text_6)
            initTerms(incOptionalSensitive, R.string.register_text_7)

            incAll.ivDetail.visibility = View.GONE
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        context?.let { ctx ->

            val act = ctx as ActRegister

            with(mBinding) {

                // 모두 동의
                incAll.ctTerms.setOnClickListener { setTermsSelected(!it.isSelected, incAll, incPrivacy, incSensitive, incOptionalSensitive) }

                // 이용 약관
                incUsage.ctTerms.setOnClickListener { controlTermsSelected(incUsage, !it.isSelected) }
                incUsage.ivDetail.setOnClickListener { act.moveToTerms(BaseConstants.TERMS_TYPE_USAGE) }

                // 개인 정보
                incPrivacy.ctTerms.setOnClickListener { controlTermsSelected(incPrivacy, !it.isSelected) }
                incPrivacy.ivDetail.setOnClickListener { act.moveToTerms(BaseConstants.TERMS_TYPE_PRIVACY) }

                // 민감 정보
                incSensitive.ctTerms.setOnClickListener { controlTermsSelected(incSensitive, !it.isSelected) }
                incSensitive.ivDetail.setOnClickListener { act.moveToTerms(BaseConstants.TERMS_TYPE_SENSITIVE) }

                // 개인 정보 제3자 제공 동의
                incOptionalSensitive.ctTerms.setOnClickListener { controlTermsSelected(incOptionalSensitive, !it.isSelected) }
                incOptionalSensitive.ivDetail.setOnClickListener { act.moveToTerms(BaseConstants.TERMS_TYPE_OPTIONAL_SENSITIVE) }
            }
        }
    }

    /**
     * Initialize Terms
     *
     * @param view
     * @param textId
     */
    private fun initTerms(view: IncRegisterTermsBinding, textId: Int) {

        view.apply {

            ivCheck.isSelected = false
            tvTerms.text = getString(textId)
        }
    }

    /**
     * Set Terms Selected
     *
     * @param isSelected
     * @param view
     */
    private fun setTermsSelected(isSelected: Boolean, vararg view: IncRegisterTermsBinding) {

        view.forEach { controlTermsSelected(it, isSelected) }
    }

    /**
     * Check Terms Selected
     *
     * @return Boolean
     */
    private fun checkTermsSelected(): Boolean {

        with(mBinding) {

            return incUsage.ctTerms.isSelected && incPrivacy.ctTerms.isSelected && incSensitive.ctTerms.isSelected
        }
    }

    /**
     * Control Terms Selected
     *
     * @param view
     * @param isSelected
     */
    private fun controlTermsSelected(view: IncRegisterTermsBinding, isSelected: Boolean) {

        view.apply {

            ctTerms.isSelected = isSelected
            ivCheck.isSelected = ctTerms.isSelected

            // 버튼 활성화 여부 설정
            context?.let { if (it is ActRegister) it.controlButtonEnabled(checkTermsSelected()) }
        }
    }
}