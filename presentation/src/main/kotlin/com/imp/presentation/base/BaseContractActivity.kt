package com.imp.presentation.base

import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

/**
 * Base Contract Activity
 */
abstract class BaseContractActivity<B: ViewBinding> : BaseActivity<B>() {

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) setFullScreen()
    }

    /**
     * ViewModel Factory Lazy
     */
    inline fun <reified viewModel : ViewModel> AppCompatActivity.createViewModel(factory: ViewModelProvider.Factory? = null): viewModel {
        return if (factory == null) {
            ViewModelProvider(this)[viewModel::class.java]
        } else {
            ViewModelProvider(this, factory)[viewModel::class.java]
        }
    }

    /**
     * fullscreen
     */
    private fun setFullScreen() {

        if (!fullScreen) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp

            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        } else {

            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }
}