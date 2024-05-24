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

                // select
                tvContents.text = contents
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