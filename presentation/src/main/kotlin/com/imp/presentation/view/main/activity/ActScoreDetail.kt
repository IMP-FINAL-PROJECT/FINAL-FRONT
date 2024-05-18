package com.imp.presentation.view.main.activity

import android.widget.Toast
import androidx.activity.viewModels
import com.github.mikephil.charting.data.LineData
import com.imp.domain.model.LogModel
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActScoreDetailBinding
import com.imp.presentation.viewmodel.LogViewModel
import com.imp.presentation.widget.extension.setMaxSize
import com.imp.presentation.widget.extension.toFloatArray
import com.imp.presentation.widget.utils.ChartUtil
import com.imp.presentation.widget.utils.DateUtil
import com.imp.presentation.widget.utils.PreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

/**
 * Score Detail Activity
 */
@AndroidEntryPoint
class ActScoreDetail : BaseContractActivity<ActScoreDetailBinding>() {

    /** Log ViewModel */
    private val viewModel: LogViewModel by viewModels()

    /** Calendar */
    private var calendar: Calendar = Calendar.getInstance()

    override fun getViewBinding() = ActScoreDetailBinding.inflate(layoutInflater)

    override fun initData() {

    }

    override fun initView() {

        initObserver()
        initDisplay()
        setOnClickListener()

        // todo 임시 log data api 호출
        val id = PreferencesUtil.getPreferencesString(this@ActScoreDetail, PreferencesUtil.AUTO_LOGIN_ID_KEY)
        viewModel.loadData(id, DateUtil.calendarToServerFormat(calendar))
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Log Data */
        viewModel.logData.observe(this) {

            setScoreGraph(it)
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

            // Header
            incHeader.tvTitle.text = getString(R.string.score_text_1)
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // 뒤로 가기
            incHeader.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    /**
     * 행복 점수 그래프
     *
     * @param chartData
     */
    private fun setScoreGraph(chartData: LogModel) {

        mBinding.chart.apply {

            // 차트
            val dataList = chartData.day.illuminance
            val lineDataset = ChartUtil.lineChartDataSet(this@ActScoreDetail, ChartUtil.mappingEntryData(dataList.toFloatArray().setMaxSize(false)))

            val max = if (dataList.isEmpty()) 0f else dataList.max().toFloat()
            val yMax = if (max < 2f) 2f else max

            data = LineData(lineDataset)
            setAxisLeft(ChartUtil.getLabelCount(max), 0f, yMax)
            setChartWeek(false, calendar)
            invalidate()
            animateY(300)
        }
    }
}