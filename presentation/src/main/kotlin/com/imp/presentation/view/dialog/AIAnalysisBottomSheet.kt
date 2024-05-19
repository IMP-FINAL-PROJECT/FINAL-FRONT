package com.imp.presentation.view.dialog

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.imp.presentation.R
import com.imp.presentation.base.BaseBottomSheetFragment
import com.imp.presentation.databinding.SheetAiAnalysisBinding
import com.imp.presentation.widget.extension.toDp
import com.imp.presentation.widget.utils.MethodStorageUtil

/**
 * AI Analysis Result BottomSheet
 */
class AIAnalysisBottomSheet(private val name: String?, private val contents: String?) : BaseBottomSheetFragment<SheetAiAnalysisBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = SheetAiAnalysisBinding.inflate(inflater, container, false)

    override fun initData() {

        isFullScreen = false
        setStyle(STYLE_NORMAL, R.style.BottomModalDialogTheme)
    }

    override fun initView() {

        initDisplay()
        setOnClickListener()
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        context?.let { ctx ->

            with(mBinding) {

                // title
                tvTitle.text = getString(R.string.analysis_text_30, name)
                MethodStorageUtil.setSpannable(tvTitle, 0, name?.length ?: 0, 20.toDp(ctx).toInt(), R.font.suit_extrabold, ContextCompat.getColor(ctx, R.color.color_3377ff))

                // todo
                // select
                tvContents.text = contents ?: "2024년 5월 1일, 사용자(test@test.com)는 하루 동안 집에 머물지 않고 한 장소(위도 37.45184681333345, 경도 127.1686598679115)에만 있었으며, 생활 루틴이 일정하지 않았습니다. 낮 동안에는 전화 사용, 통화, 걸음 수, 빛 노출 등이 모두 없었지만, 밤에는 전화 사용 빈도가 2.5회, 전화 사용 시간이 약 16분 51초였으며, 밤 동안의 빛 노출 시간은 약 12시간 27분이었습니다. 걸음 수는 밤 동안 약 7.67걸음에 불과했습니다. 전반적으로, 사용자는 밤에 주로 활동하며 낮에는 거의 활동이 없었던 것으로 보입니다."
                tvContents.movementMethod = ScrollingMovementMethod.getInstance()
            }
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 닫기
            ivClose.setOnClickListener { dismiss() }
        }
    }
}