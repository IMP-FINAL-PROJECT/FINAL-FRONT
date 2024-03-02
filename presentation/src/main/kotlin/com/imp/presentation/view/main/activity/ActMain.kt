package com.imp.presentation.view.main.activity

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.imp.presentation.R
import com.imp.presentation.base.BaseContractActivity
import com.imp.presentation.databinding.ActMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity
 */
@AndroidEntryPoint
class ActMain : BaseContractActivity<ActMainBinding>() {

    override fun getViewBinding() = ActMainBinding.inflate(layoutInflater)

    override fun initData() {

    }

    override fun initView() {

        initNavigationBar()
    }

    /**
     * Initialize Bottom Navigation Bar
     */
    private fun initNavigationBar() {

        with(mBinding) {

            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)
            val navController = (navHostFragment as NavHostFragment).findNavController()

            navigationBar.itemIconTintList = null
            navigationBar.setupWithNavController(navController)
        }
    }
}