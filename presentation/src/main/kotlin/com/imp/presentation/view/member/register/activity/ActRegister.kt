package com.imp.presentation.view.member.register.activity

import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.databinding.ActMemberRegisterBinding
import com.imp.presentation.view.adatper.ViewPagerAdapter
import com.imp.presentation.view.member.register.fragment.FrgEmail
import com.imp.presentation.view.member.register.fragment.FrgPassword
import com.imp.presentation.view.member.register.fragment.FrgProfile
import com.imp.presentation.view.member.register.fragment.FrgTerms
import com.imp.presentation.viewmodel.MemberViewModel
import com.imp.presentation.widget.extension.hideKeyboard
import com.imp.presentation.widget.utils.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * Register Activity
 */
@AndroidEntryPoint
class ActRegister : BaseContractActivity<ActMemberRegisterBinding>() {

    /** Member ViewModel */
    private val viewModel: MemberViewModel by viewModels()

    /** ViewPager 관련 변수 */
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private val fragments: ArrayList<BaseFragment<*>> = ArrayList()
    private var currentPosition: Int = 0

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

        initObserver()
        initDisplay()
        initViewPager()
        setOnClickListener()

        // 항상 허용으로 변경 요청
        showCommonPopup(
            titleText = getString(R.string.popup_text_2),
            leftText = getString(R.string.permission_text_11),
            rightText = getString(R.string.permission_text_11),
            leftCallback = { },
            rightCallback = { },
            cancelable = false
        )
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Register Response Data */
        viewModel.registerResData.observe(this) { _ ->

            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()

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

            incHeader.tvTitle.visibility = View.GONE

            tvRegisterTitle.text = getString(R.string.register_text_1)

            // 다음 / 회원 가입 버튼
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

            fragments.add(FrgTerms.newInstance())
            fragments.add(FrgEmail.newInstance())
            fragments.add(FrgPassword.newInstance())
            fragments.add(FrgProfile.newInstance())

            viewPagerAdapter = ViewPagerAdapter(this@ActRegister, fragments)
            viewPager.adapter = viewPagerAdapter
            viewPager.isUserInputEnabled = false
            viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    fragments[position].reLoad()

                    // UI 초기화
                    setUI()
                }
            })
        }
    }

    /**
     * Set onClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 뒤로 가기
            incHeader.ivBack.setOnClickListener {

//                if (currentPosition > 0) {
//
//                    // 이전 화면 이동
//                    currentPosition--
//                    viewPager.setCurrentItem(currentPosition, false)
//
//                } else {
//
//                    // 뒤로 가기
//                    onBackPressedDispatcher.onBackPressed()
//                }

                // 뒤로 가기
                onBackPressedDispatcher.onBackPressed()
            }

            // 다음 / 회원 가입 버튼
            incNextAndRegister.tvButton.setOnClickListener {

                if (currentPosition < fragments.size - 1) {

                    // 다음 화면 이동
                    currentPosition++
                    viewPager.setCurrentItem(currentPosition, false)

                } else {

                    // 회원 가입 api 호출
                    viewModel.register()
                }

                // 키보드 내리기
                hideKeyboard(this@ActRegister, currentFocus)
            }
        }
    }

    /**
     * UI 초기화
     */
    private fun setUI() {

        with(mBinding) {

            // 타이틀 설정
            if (currentPosition == 0) {
                tvRegisterTitle.text = getString(R.string.register_text_2)
            } else {
                tvRegisterTitle.text = getString(R.string.register_text_1)
            }

            // 버튼 비 활성화
            incNextAndRegister.tvButton.isEnabled = false

            // 버튼 타이틀 설정
            if (currentPosition == fragments.size - 1) {
                incNextAndRegister.tvButton.text = getString(R.string.register_text_1)
            } else {
                incNextAndRegister.tvButton.text = getString(R.string.next)
            }
        }
    }

    /**
     * Control Button Enabled
     */
    fun controlButtonEnabled(isEnabled: Boolean) {
        mBinding.incNextAndRegister.tvButton.isEnabled = isEnabled
    }
}