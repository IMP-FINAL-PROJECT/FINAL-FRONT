package com.imp.presentation.view.member.register.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgRegisterProfileBinding
import com.imp.presentation.view.member.register.activity.ActRegister
import com.imp.presentation.viewmodel.MemberViewModel
import com.imp.presentation.widget.extension.toVisibleOrGone
import com.imp.presentation.widget.utils.DateUtil
import com.imp.presentation.widget.utils.ValidateUtil


/**
 * Register - Profile Fragment
 */
class FrgProfile: BaseFragment<FrgRegisterProfileBinding>() {

    companion object {

        fun newInstance(): FrgProfile {
            return FrgProfile().apply {
                arguments = Bundle().apply {}
            }
        }
    }

    /** Member ViewModel */
    private val viewModel: MemberViewModel by activityViewModels()

    // current birth text
    private var currentBirthText = ""

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgRegisterProfileBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        initDisplay()
        setOnClickListener()
        initEditText()
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // Name
            incName.apply {

                tvTitle.text = getString(R.string.register_text_11)
                ivCancel.visibility = View.GONE
            }

            // Birth
            incBirth.apply {

                tvTitle.text = getString(R.string.register_text_13)
                ivCancel.visibility = View.GONE
            }

            // Address
            incAddress.apply {

                tvTitle.text = getString(R.string.register_text_19)
                ivCancel.visibility = View.GONE

                // todo: 임시로 비노출
                ctRoot.visibility = View.GONE
            }

            // Gender
            incGender.apply {

                tvTitle.text = getString(R.string.register_text_14)
                tvMale.text = getString(R.string.register_text_15)
                tvFemale.text = getString(R.string.register_text_16)
                tvNone.text = getString(R.string.register_text_17)
            }
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // name 전체 삭제
            incName.ivCancel.setOnClickListener { incName.etInput.text = null }

            // birth 전체 삭제
            incBirth.ivCancel.setOnClickListener { incBirth.etInput.text = null }

            // address 전체 삭제
            incAddress.ivCancel.setOnClickListener { incAddress.etInput.text = null }

            // 남성
            incGender.tvMale.setOnClickListener { setGenderSelected(BaseConstants.GENDER_TYPE_MALE) }

            // 여성
            incGender.tvFemale.setOnClickListener { setGenderSelected(BaseConstants.GENDER_TYPE_FEMALE) }

            // 선택 하지 않음
            incGender.tvNone.setOnClickListener { setGenderSelected(BaseConstants.GENDER_TYPE_NONE) }
        }
    }

    /**
     * Initialize EditText
     */
    private fun initEditText() {

        with(mBinding) {

            // name 입력
            incName.etInput.apply {

                isSingleLine = true
                hint = getString(R.string.register_text_12)
                inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME

                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                        val value = p0.toString()

                        // 전체 삭제 버튼 노출 여부
                        incName.ivCancel.visibility = value.isNotEmpty().toVisibleOrGone()

                        // check profile validate
                        checkProfileValidate()
                    }
                })
            }

            // birth 입력
            incBirth.etInput.apply {

                isSingleLine = true
                hint = getString(R.string.register_text_18)
                inputType = InputType.TYPE_CLASS_NUMBER

                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                        val value = p0.toString()

                        if (value.isNotEmpty() && value != currentBirthText) {

                            val clearValue = value.replace(".", "")
                            currentBirthText = setBirthStringBuilder(clearValue)

                            setText(currentBirthText)
                            Selection.setSelection(text, currentBirthText.length)
                        }

                        // 전체 삭제 버튼 노출 여부
                        incBirth.ivCancel.visibility = value.isNotEmpty().toVisibleOrGone()

                        // check profile validate
                        checkProfileValidate()
                    }
                })
            }

            // address 입력
            incAddress.etInput.apply {

                isSingleLine = true
                hint = getString(R.string.register_text_20)
                inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME

                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(p0: Editable?) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                        val value = p0.toString()

                        // 전체 삭제 버튼 노출 여부
                        incAddress.ivCancel.visibility = value.isNotEmpty().toVisibleOrGone()

                        // check profile validate
                        checkProfileValidate()
                    }
                })
            }
        }
    }

    /**
     * Set Birth StringBuilder
     *
     * @param str
     * @return
     */
    fun setBirthStringBuilder(str: String?): String {

        if (str.isNullOrEmpty()) return ""

        val stringBuilder = StringBuilder()
        stringBuilder.append(str)

        if (str.length > 4) stringBuilder.insert(4, ".")
        if (str.length > 7) stringBuilder.insert(7, ".")

        return stringBuilder.toString()
    }

    /**
     * Control Gender Selected
     *
     * @param type
     */
    private fun setGenderSelected(type: String) {

        with(mBinding.incGender) {

            when(type) {

                // 남성
                BaseConstants.GENDER_TYPE_MALE -> {

                    tvMale.isSelected = true
                    tvFemale.isSelected = false
                    tvNone.isSelected = false
                }

                // 여성
                BaseConstants.GENDER_TYPE_FEMALE -> {

                    tvMale.isSelected = false
                    tvFemale.isSelected = true
                    tvNone.isSelected = false
                }

                // 선택 하지 않음
                BaseConstants.GENDER_TYPE_NONE -> {

                    tvMale.isSelected = false
                    tvFemale.isSelected = false
                    tvNone.isSelected = true
                }
            }

            // check profile validate
            checkProfileValidate()
        }
    }

    /**
     * Check Profile Validate
     */
    private fun checkProfileValidate() {

        with(mBinding) {

            val name = incName.etInput.text.toString()
            val birth = incBirth.etInput.text.toString().replace(".", "")
            val address = incAddress.etInput.text.toString()
            val gender = if (incGender.tvMale.isSelected) {
                BaseConstants.GENDER_TYPE_MALE
            } else if (incGender.tvFemale.isSelected) {
                BaseConstants.GENDER_TYPE_FEMALE
            } else {
                BaseConstants.GENDER_TYPE_NONE
            }

            val nameValidate = name.isNotEmpty()
            val birthValidate = ValidateUtil.checkBirth(birth)
            val addressValidate = address.isNotEmpty()
            val genderValidate = incGender.tvMale.isSelected || incGender.tvFemale.isSelected || incGender.tvNone.isSelected

            if (nameValidate && birthValidate && genderValidate) {

                viewModel.registerData.name = name
                viewModel.registerData.birth = DateUtil.stringToDate(birth)
                viewModel.registerData.gender = gender
            }

            // 버튼 활성화 여부 설정
            context?.let { if (it is ActRegister) it.controlButtonEnabled(nameValidate && birthValidate && genderValidate) }
        }
    }
}