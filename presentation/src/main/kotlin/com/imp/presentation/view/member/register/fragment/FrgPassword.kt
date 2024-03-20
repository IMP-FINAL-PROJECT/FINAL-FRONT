package com.imp.presentation.view.member.register.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.databinding.FrgRegisterPasswordBinding
import com.imp.presentation.databinding.IncCommonEdittextBinding
import com.imp.presentation.view.member.register.activity.ActRegister
import com.imp.presentation.viewmodel.MemberViewModel
import com.imp.presentation.widget.extension.focusAndShowKeyboard
import com.imp.presentation.widget.extension.toGoneOrVisible
import com.imp.presentation.widget.extension.toVisibleOrGone
import com.imp.presentation.widget.utils.ValidateUtil

/**
 * Register - Password Fragment
 */
class FrgPassword: BaseFragment<FrgRegisterPasswordBinding>() {

    companion object {

        fun newInstance(): FrgPassword {
            return FrgPassword().apply {
                arguments = Bundle().apply {}
            }
        }
    }

    /** Member ViewModel */
    private val viewModel: MemberViewModel by activityViewModels()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgRegisterPasswordBinding.inflate(inflater, container, false)

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

            // Password
            incPassword.tvTitle.text = getString(R.string.login_text_4)
            incPassword.ivCancel.visibility = View.GONE

            // Password
            incPasswordConfirm.tvTitle.text = getString(R.string.register_text_8)
            incPasswordConfirm.ivCancel.visibility = View.GONE

            // validate
            tvValidate.text = getString(R.string.register_text_9)

            // notice
            ctNotice.visibility = View.GONE
            tvNotice.text = getString(R.string.register_text_10)

            // editText 초기화
            initEditText(incPassword)
            initEditText(incPasswordConfirm)

            // 화면 진입 시 키보드 올리기
            context?.let { incPassword.etInput.focusAndShowKeyboard(it) }
        }
    }

    /**
     * Set onClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // password 전체 삭제
            incPassword.ivCancel.setOnClickListener { incPassword.etInput.text = null }

            // password 숨기기/보기 클릭
            incPassword.ivVisibility.setOnClickListener { setPasswordVisibility(incPassword) }

            // password Confirm 전체 삭제
            incPasswordConfirm.ivCancel.setOnClickListener { incPasswordConfirm.etInput.text = null }

            // password Confirm 숨기기/보기 클릭
            incPasswordConfirm.ivVisibility.setOnClickListener { setPasswordVisibility(incPasswordConfirm) }
        }
    }

    /**
     * Initialize EditText
     */
    private fun initEditText(view: IncCommonEdittextBinding) {

        // password 입력
        view.etInput.apply {

            isSingleLine = true
            hint = getString(R.string.login_text_5)
            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    val value = p0.toString()

                    // 전체 삭제 버튼 노출 여부
                    view.ivCancel.visibility = value.isNotEmpty().toVisibleOrGone()
                    view.ivVisibility.visibility = value.isNotEmpty().toVisibleOrGone()

                    // check password validate
                    checkPasswordValidate()
                }
            })
        }
    }

    /**
     * Check Password Validate
     */
    private fun checkPasswordValidate() {

        with(mBinding) {

            val password = incPassword.etInput.text.toString()
            val passwordConfirm = incPasswordConfirm.etInput.text.toString()

            val isEquals = password == passwordConfirm
            val validate = ValidateUtil.checkPassword(password) || ValidateUtil.checkPassword(passwordConfirm)

            // notice
            ctNotice.visibility = isEquals.toGoneOrVisible()

            // 비밀 번호 저장
            if (isEquals && validate) {
                viewModel.registerData.password = password
            }

            // 버튼 활성화 여부 설정
            context?.let { if (it is ActRegister) it.controlButtonEnabled(isEquals && validate) }
        }
    }

    /**
     * Set Password Visibility
     *
     * @param view
     */
    private fun setPasswordVisibility(view: IncCommonEdittextBinding) {

        view.apply {

            ivVisibility.isSelected = !ivVisibility.isSelected

            // password 노출 여부
            etInput.transformationMethod = if (ivVisibility.isSelected) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }

            etInput.setSelection(etInput.length())
        }
    }
}