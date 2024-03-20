package com.imp.presentation.view.member.login

import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActMemberLoginBinding
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.viewmodel.MemberViewModel
import com.imp.presentation.widget.extension.focusAndShowKeyboard
import com.imp.presentation.widget.extension.hideKeyboard
import com.imp.presentation.widget.extension.toVisibleOrGone
import com.imp.presentation.widget.utils.PreferencesUtil
import com.imp.presentation.widget.utils.ValidateUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * Login Activity
 */
@AndroidEntryPoint
class ActLogin : BaseContractActivity<ActMemberLoginBinding>() {

    /** Member ViewModel */
    private val viewModel: MemberViewModel by viewModels()

    // login data
    private var userId: String = ""
    private var userPassword: String = ""

    override fun getViewBinding() = ActMemberLoginBinding.inflate(layoutInflater)

    override fun initData() {

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                hideKeyboard(this@ActLogin, currentFocus)
                activityCloseAnimation()
                finish()
            }
        }
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

        /** Login Data */
        viewModel.loginData.observe(this) { _ ->

            // id, password local 저장
            PreferencesUtil.setPreferencesString(this, PreferencesUtil.AUTO_LOGIN_ID_KEY, userId)
            PreferencesUtil.setPreferencesString(this, PreferencesUtil.AUTO_LOGIN_PASSWORD_KEY, userPassword)

            // 메인 화면 이동
            moveToMain()
        }

        /** Error Callback */
        viewModel.errorCallback.observe(this) { event ->
            event.getContentIfNotHandled()?.let { error ->

                when (error.status) {

                    401 -> mBinding.ctNotice.visibility = View.VISIBLE
                    else -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            incHeader.tvTitle.visibility = View.GONE

            tvLoginTitle.text = getString(R.string.login_text_1)

            // Email
            incEmail.tvTitle.text = getString(R.string.login_text_2)
            incEmail.ivCancel.visibility = View.GONE

            // Password
            incPassword.tvTitle.text = getString(R.string.login_text_4)
            incPassword.ivCancel.visibility = View.GONE

            // notice
            ctNotice.visibility = View.GONE
            tvNotice.text = getString(R.string.login_text_6)

            // 로그인 버튼
            incLogin.apply {

                tvButton.text = getString(R.string.login_text_1)
                tvButton.isEnabled = false
            }
        }
    }

    /**
     * Set onClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 뒤로 가기
            incHeader.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            // email 전체 삭제
            incEmail.ivCancel.setOnClickListener { incEmail.etInput.text = null }

            // password 전체 삭제
            incPassword.ivCancel.setOnClickListener { incPassword.etInput.text = null }

            // password 숨기기/보기 클릭
            incPassword.ivVisibility.setOnClickListener {

                it.isSelected = !it.isSelected

                // password 노출 여부
                incPassword.etInput.transformationMethod = if (it.isSelected) {
                    HideReturnsTransformationMethod.getInstance()
                } else {
                    PasswordTransformationMethod.getInstance()
                }

                incPassword.etInput.setSelection(incPassword.etInput.length())
            }

            // 로그인
            incLogin.tvButton.setOnClickListener {

                // 키보드 내리기
                hideKeyboard(this@ActLogin, currentFocus, incEmail.etInput, incPassword.etInput)

                // id, password 저장
                userId = incEmail.etInput.text.toString()
                userPassword = incPassword.etInput.text.toString()

                // login api 호츌
                viewModel.login(userId, userPassword)
            }
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

                        // check login validate
                        checkLoginEValidate()
                    }
                })
            }

            // password 입력
            incPassword.etInput.apply {

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
                        incPassword.ivCancel.visibility = value.isNotEmpty().toVisibleOrGone()
                        incPassword.ivVisibility.visibility = value.isNotEmpty().toVisibleOrGone()

                        // check login validate
                        checkLoginEValidate()
                    }
                })
            }

            // 화면 진입 시 키보드 올리기
            incEmail.etInput.focusAndShowKeyboard(this@ActLogin)
        }
    }

    /**
     * Check Login Button Validate
     */
    private fun checkLoginEValidate() {

        with(mBinding) {

            val email = incEmail.etInput.text.toString()
            val password = incPassword.etInput.text.toString()

            incLogin.tvButton.isEnabled = ValidateUtil.checkEmail(email) && ValidateUtil.checkPassword(password)
            ctNotice.visibility = View.GONE
        }
    }

    /**
     * Move to Main
     */
    private fun moveToMain() {

        // 메인 화면 이동
        Intent(this@ActLogin, ActMain::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }

        finishAffinity()
    }
}