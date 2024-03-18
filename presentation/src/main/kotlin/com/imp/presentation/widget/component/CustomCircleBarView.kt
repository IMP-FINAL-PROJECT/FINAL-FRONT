package com.imp.presentation.widget.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.imp.presentation.R
import com.imp.presentation.widget.utils.CommonUtil

/**
 * Custom Circle Progress Bar
 */
class CustomCircleBarView: View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private var progress = 200f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paint = Paint()

        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = CommonUtil.pxFromDp(context, 12f)
        paint.color = ContextCompat.getColor(context, R.color.color_bdd3ff)
        canvas.drawArc(CommonUtil.pxFromDp(context, 6f), CommonUtil.pxFromDp(context, 6f), CommonUtil.pxFromDp(context, 120f), CommonUtil.pxFromDp(context, 120f), 0f, 360f, false, paint)

        paint.strokeCap = Paint.Cap.ROUND
        paint.color = ContextCompat.getColor(context, R.color.color_3377ff)
        canvas.drawArc(CommonUtil.pxFromDp(context, 6f), CommonUtil.pxFromDp(context, 6f), CommonUtil.pxFromDp(context, 120f), CommonUtil.pxFromDp(context, 120f), -90f, progress, false, paint)
    }

    fun setProgress(percent: Int) {

        progress = (360 * percent / 100).toFloat()

        invalidate()
    }
}