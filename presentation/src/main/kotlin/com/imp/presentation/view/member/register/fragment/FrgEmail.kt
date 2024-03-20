package com.imp.presentation.view.member.register.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.databinding.FrgRegisterEmailBinding
import com.imp.presentation.view.member.register.activity.ActRegister
import com.imp.presentation.viewmodel.MemberViewModel
import com.imp.presentation.widget.extension.focusAndShowKeyboard
import com.imp.presentation.widget.extension.toVisibleOrGone
import com.imp.presentation.widget.utils.ValidateUtil

/**
 * Register - Email Fragment
 */
class FrgEmail: BaseFragment<FrgRegisterEmailBinding>() {

    companion object {

        fun newInstance(): FrgEmail {
            return FrgEmail().apply {
                arguments = Bundle().apply {}
            }
        }
    }

    /** Member ViewModel */
    private val viewModel: MemberViewModel by activityViewModels()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgRegisterEmailBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        initObserver()
        initDisplay()
        setOnClickListener()
        initEditText()
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Email Validation */
        viewModel.emailValidationData.observe(viewLifecycleOwner) { result ->

            // 버튼 활성화 여부 설정
            context?.let { if (it is ActRegister) it.controlButtonEnabled(result) }
        }

        /** Error Callback */
        viewModel.errorCallback.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { error ->

            }
        }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // Email
            incEmail.tvTitle.text = getString(R.string.login_text_2)
            incEmail.ivCancel.visibility = View.GONE
        }
    }

    /**
     * Set onClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // email 전체 삭제
            incEmail.ivCancel.setOnClickListener { incEmail.etInput.text = null }
        }
    }

    /**
     * Initialize EditText
     */
    private fun initEditText() {

        with(mBinding) {

            // email 입력
            incEmail.etInput.apply {

                isSingleLine = true
                hint = getString(R.string.login_text_3)
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                        val value = p0.toString()

                        // 전체 삭제 버튼 노출 여부
                        incEmail.ivCancel.visibility = value.isNotEmpty().toVisibleOrGone()

                        // check email validate
                        checkEmailValidate()
                    }
                })
            }

            // 화면 진입 시 키보드 올리기
            context?.let { incEmail.etInput.focusAndShowKeyboard(it) }
        }
    }

    /**
     * Check Email Validate
     */
    private fun checkEmailValidate() {

        with(mBinding) {

            val email = incEmail.etInput.text.toString()

            if (ValidateUtil.checkEmail(email)) {

                // email 양식 충족 시 api 요청
                viewModel.checkEmail(email)
                viewModel.registerData.id = email
            }
        }
    }
}