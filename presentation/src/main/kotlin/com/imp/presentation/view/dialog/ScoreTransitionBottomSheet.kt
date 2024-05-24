package com.imp.presentation.view.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.mikephil.charting.data.LineData
import com.imp.presentation.R
import com.imp.presentation.base.BaseBottomSheetFragment
import com.imp.presentation.databinding.SheetScoreTransitionBinding
import com.imp.presentation.widget.extension.setMaxSize
import com.imp.presentation.widget.extension.toFloatArray
import com.imp.presentation.widget.utils.ChartUtil
import com.imp.presentation.widget.utils.DateUtil
import java.util.Calendar

/**
 * Score Transition BottomSheet
 */
class ScoreTransitionBottomSheet(private val pointList: ArrayList<Int>) : BaseBottomSheetFragment<SheetScoreTransitionBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = SheetScoreTransitionBinding.inflate(inflater, container, false)

    override fun initData() {

        isFullScreen = false
        setStyle(STYLE_NORMAL, R.style.BottomModalDialogTheme)
    }

    override fun initView() {

        initDisplay()
        setScoreGraph()
        setOnClickListener()
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // title
            tvTitle.text = getString(R.string.home_text_7)

            // date
            tvDate.text = DateUtil.getWeeklyWithLast(Calendar.getInstance())

            // description
            tvDescription.text = getString(R.string.home_text_8)
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

    /**
     * 행복 점수 그래프
     */
    private fun setScoreGraph() {

        context?.let { ctx ->

            mBinding.chart.apply {

                // 차트
                val lineDataset = ChartUtil.lineChartDataSet(ctx, ChartUtil.mappingEntryData(pointList.toFloatArray().setMaxSize(false)))

                val max = if (pointList.isEmpty()) 0f else pointList.max().toFloat()
                val yMax = if (max < 2f) 2f else max

                data = LineData(lineDataset)
                setAxisLeft(ChartUtil.getLabelCount(max), 0f, yMax)
                setChartWeek(false, Calendar.getInstance())
                invalidate()
                animateY(300)
            }
        }
    }
}