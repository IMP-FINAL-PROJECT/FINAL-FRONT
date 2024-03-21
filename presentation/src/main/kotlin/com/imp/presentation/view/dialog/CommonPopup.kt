package com.imp.presentation.view.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.imp.presentation.R
import com.imp.presentation.databinding.PopupCommonBinding
import com.imp.presentation.widget.extension.toGoneOrVisible

/**
 * Common Popup
 */
class CommonPopup(
    context: Context,
    private val titleText: String,
    private val leftText: String? = null,
    private val rightText: String? = null,
    private val leftCallback: () -> Unit = {},
    private val rightCallback: () -> Unit = {},
    private val cancelable: Boolean
) : Dialog(context, R.style.AppTheme_Dialog) {

    lateinit var mBinding: PopupCommonBinding

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.let {

            it.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            it.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            it.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        mBinding = PopupCommonBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setCancelable(cancelable)

        setFullScreen()
        initDisplay()
        setOnClickListener()
    }

    /** fullScreen */
    private fun setFullScreen() {

        window?.let {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                val lp = it.attributes
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                it.attributes = lp

                it.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            } else {

                it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            tvTitle.text = titleText

            // cancel (left)
            tvCancel.text = leftText
            tvCancel.visibility = leftText.isNullOrEmpty().toGoneOrVisible()

            // confirm (right)
            tvConfirm.text = rightText
            tvConfirm.visibility = rightText.isNullOrEmpty().toGoneOrVisible()
        }
    }

    /**
     * Initialize setOnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            // cancel (left)
            tvCancel.setOnClickListener {

                leftCallback.invoke()
                dismiss()
            }

            // confirm (right)
            tvConfirm.setOnClickListener {

                rightCallback.invoke()
                dismiss()
            }
        }
    }
}