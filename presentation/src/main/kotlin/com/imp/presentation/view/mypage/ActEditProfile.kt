package com.imp.presentation.view.mypage

import android.text.Editable
import android.text.InputType
import android.text.Selection
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.imp.domain.model.MemberModel
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.ActMypageEditProfileBinding
import com.imp.presentation.viewmodel.MemberViewModel
import com.imp.presentation.widget.extension.hideKeyboard
import com.imp.presentation.widget.extension.toVisibleOrGone
import com.imp.presentation.widget.utils.PreferencesUtil
import com.imp.presentation.widget.utils.ValidateUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * Edit Profile Activity
 */
@AndroidEntryPoint
class ActEditProfile : BaseContractActivity<ActMypageEditProfileBinding>() {

    /** Member ViewModel */
    private val viewModel: MemberViewModel by viewModels()

    // current birth text
    private var currentBirthText = ""

    override fun getViewBinding() = ActMypageEditProfileBinding.inflate(layoutInflater)

    override fun initData() {

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                hideKeyboard(this@ActEditProfile, currentFocus)
                activityCloseAnimation()
                finish()
            }
        }

        // member data api 호출
        viewModel.getMember()
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

        /** Member Data */
        viewModel.memberData.observe(this) { dao ->

            initProfile(dao)
        }

        /** Edit Profile Response Data */
        viewModel.loginData.observe(this) { _ ->

            Toast.makeText(this, getString(R.string.popup_text_4), Toast.LENGTH_SHORT).show()

            // 뒤로 가기
            onBackPressedDispatcher.onBackPressed()
        }

        /** Error Callback */
        viewModel.errorCallback.observe(this) { event ->
            event.getContentIfNotHandled()?.let { error ->

                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // header
            incHeader.tvTitle.text = getString(R.string.my_page_text_3)

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
            }

            // Gender
            incGender.apply {

                tvTitle.text = getString(R.string.register_text_14)
                tvMale.text = getString(R.string.register_text_15)
                tvFemale.text = getString(R.string.register_text_16)
                tvNone.text = getString(R.string.register_text_17)
            }

            // 완료
            incComplete.tvButton.text = getString(R.string.complete)
            incComplete.tvButton.isEnabled = false
        }
    }

    /**
     * Initialize Profile
     *
     * @param dao
     */
    private fun initProfile(dao: MemberModel) {

        with(mBinding) {

            // Name
            incName.etInput.setText(dao.name)

            // Birth
            incBirth.etInput.setText(dao.birth?.replace("-", "."))

            // Address
            incAddress.etInput.setText(dao.address)

            // Gender
            incGender.apply {

                when(dao.gender) {
                    BaseConstants.GENDER_TYPE_MALE -> setGenderSelected(BaseConstants.GENDER_TYPE_MALE)
                    BaseConstants.GENDER_TYPE_FEMALE -> setGenderSelected(BaseConstants.GENDER_TYPE_FEMALE)
                    else -> setGenderSelected(BaseConstants.GENDER_TYPE_NONE)
                }
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

            // 완료
            incComplete.tvButton.setOnClickListener {

                viewModel.editProfile(
                    id = PreferencesUtil.getPreferencesString(this@ActEditProfile, PreferencesUtil.AUTO_LOGIN_ID_KEY),
                    name = incName.etInput.text.toString(),
                    birth = incBirth.etInput.text.toString().replace(".", "-"),
                    address = incAddress.etInput.text.toString(),
                    gender = getGender()
                )
            }
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

            val btnList = listOf(tvMale, tvFemale, tvNone)
            when(type) {

                // 남성
                BaseConstants.GENDER_TYPE_MALE -> btnList.forEachIndexed { index, textView -> textView.isSelected = index == 0 }

                // 여성
                BaseConstants.GENDER_TYPE_FEMALE -> btnList.forEachIndexed { index, textView -> textView.isSelected = index == 1 }

                // 선택 하지 않음
                BaseConstants.GENDER_TYPE_NONE -> btnList.forEachIndexed { index, textView -> textView.isSelected = index == 2 }
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
            val gender = getGender()

            val nameValidate = name.isNotEmpty()
            val birthValidate = ValidateUtil.checkBirth(birth)
            val addressValidate = address.isNotEmpty()
            val genderValidate = incGender.tvMale.isSelected || incGender.tvFemale.isSelected || incGender.tvNone.isSelected

            val validation = nameValidate && birthValidate && genderValidate && addressValidate
            val enabled = viewModel.memberData.value?.let {
                it.name != name || it.birth?.replace("-", "") != birth || it.gender != gender || it.address != address
            } ?: false

            // 버튼 활성화 여부
            incComplete.tvButton.isEnabled = validation && enabled
        }
    }

    /**
     * Get Gender
     *
     * @return
     */
    private fun getGender(): String {

        with(mBinding.incGender) {

            return if (tvMale.isSelected) {
                BaseConstants.GENDER_TYPE_MALE
            } else if (tvFemale.isSelected) {
                BaseConstants.GENDER_TYPE_FEMALE
            } else {
                BaseConstants.GENDER_TYPE_NONE
            }
        }
    }
}