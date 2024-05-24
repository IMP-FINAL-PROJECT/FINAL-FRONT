package com.imp.presentation.view.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.imp.domain.model.MemberModel
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgMypageBinding
import com.imp.presentation.databinding.IncMypageSettingBinding
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.viewmodel.MemberViewModel
import com.imp.presentation.widget.utils.MethodStorageUtil
import com.imp.presentation.widget.utils.PreferencesUtil

/**
 * Main - MyPage Fragment
 */
class FrgMyPage: BaseFragment<FrgMypageBinding>() {

    companion object {

        private const val TYPE_SETTING_EDIT = "TYPE_SETTING_EDIT"               // 프로필 편집
        private const val TYPE_SETTING_ACCOUNT = "TYPE_SETTING_ACCOUNT"         // 계정 관리
        private const val TYPE_SETTING_TERMS = "TYPE_SETTING_TERMS"             // 이용 약관
        private const val TYPE_SETTING_PRIVACY = "TYPE_SETTING_PRIVACY"         // 개인 정보 처리 방침
        private const val TYPE_SETTING_OPENSOURCE = "TYPE_SETTING_OPENSOURCE"   // 오픈 소스
        private const val TYPE_SETTING_PERMISSION = "TYPE_SETTING_PERMISSION"   // 앱 접근 권한
        private const val TYPE_SETTING_VERSION = "TYPE_SETTING_VERSION"         // 앱 버전
    }

    /** Member ViewModel */
    private val viewModel: MemberViewModel by activityViewModels()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgMypageBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        // set status bar color
        activity?.let { if (it is ActMain) it.setCurrentStatusBarColor(BaseConstants.MAIN_NAV_LABEL_MYPAGE) }

        // member data api 호출
        viewModel.getMember()

        initObserver()
        initDisplay()
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Member Data */
        viewModel.memberData.observe(viewLifecycleOwner) { dao ->

            initProfile(dao)
        }

        /** Error Callback */
        viewModel.errorCallback.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { error ->

                context?.let { Toast.makeText(it, error.message, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // Header
            incHeader.tvTitle.text = getString(R.string.navigation_mypage)
            incHeader.ivAddChat.visibility = View.GONE

            // 일반
            tvGeneral.text = getString(R.string.my_page_text_2)
            initSettingButton(incEditProfile, TYPE_SETTING_EDIT, getString(R.string.my_page_text_3))
            initSettingButton(incAccount, TYPE_SETTING_ACCOUNT, getString(R.string.my_page_text_4))

            // 이용 안내
            tvUsage.text = getString(R.string.my_page_text_5)
            initSettingButton(incTerms, TYPE_SETTING_TERMS, getString(R.string.my_page_text_6))
            initSettingButton(incPrivacy, TYPE_SETTING_PRIVACY, getString(R.string.my_page_text_7))
            initSettingButton(incOpenSource, TYPE_SETTING_OPENSOURCE, getString(R.string.my_page_text_8))
            initSettingButton(incPermission, TYPE_SETTING_PERMISSION, getString(R.string.my_page_text_9))
            initSettingButton(incVersion, TYPE_SETTING_VERSION, getString(R.string.my_page_text_10))
        }
    }

    /**
     * Initialize Profile
     *
     * @param dao
     */
    private fun initProfile(dao: MemberModel) {

        with(mBinding) {

            tvName.text = dao.name
            tvEmail.text = dao.id
        }
    }

    /**
     * Initialize Setting Button
     *
     * @param view
     * @param type
     * @param title
     */
    private fun initSettingButton(view: IncMypageSettingBinding, type: String, title: String) {

        context?.let { ctx->

            view.apply {

                tvTitle.text = title

                if (type == TYPE_SETTING_VERSION) {

                    tvVersion.text = MethodStorageUtil.getPackageVersion(ctx)
                    tvVersion.visibility = View.VISIBLE
                    ivArrow.visibility = View.GONE

                } else {

                    tvVersion.visibility = View.VISIBLE
                    ivArrow.visibility = View.GONE
                }

                // 설정 버튼
                llContents.setOnClickListener { buttonClickEvent(type) }
            }
        }
    }

    /**
     * Setting Button Click Event
     *
     * @param type
     */
    private fun buttonClickEvent(type: String) {

        context?.let { ctx->

            val actMain = ctx as ActMain
            when(type) {

                TYPE_SETTING_EDIT -> actMain.moveToEditProfile()
                TYPE_SETTING_ACCOUNT -> actMain.moveToManageAccount()
                TYPE_SETTING_TERMS -> actMain.moveToTerms(BaseConstants.TERMS_TYPE_USAGE)
                TYPE_SETTING_PRIVACY -> actMain.moveToTerms(BaseConstants.TERMS_TYPE_PRIVACY)
                TYPE_SETTING_PERMISSION -> actMain.moveToPermission()
                else -> return
            }
        }
    }
}