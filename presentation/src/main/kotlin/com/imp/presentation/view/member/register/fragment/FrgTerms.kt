package com.imp.presentation.view.member.register.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
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
            initTerms(incPrivacy, R.string.register_text_5)
            initTerms(incSensitive, R.string.register_text_6)
            initTerms(incUse, R.string.register_text_7)
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 모두 동의
            incAll.ctTerms.setOnClickListener { setTermsSelected(!it.isSelected, incAll, incPrivacy, incSensitive, incUse) }

            // 개인 정보
            incPrivacy.ctTerms.setOnClickListener { controlTermsSelected(incPrivacy, !it.isSelected) }

            // 민감 정보
            incSensitive.ctTerms.setOnClickListener { controlTermsSelected(incSensitive, !it.isSelected) }

            // 개인 정보 제3자 제공 동의
            incUse.ctTerms.setOnClickListener { controlTermsSelected(incUse, !it.isSelected) }
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

            return incPrivacy.ctTerms.isSelected && incSensitive.ctTerms.isSelected
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