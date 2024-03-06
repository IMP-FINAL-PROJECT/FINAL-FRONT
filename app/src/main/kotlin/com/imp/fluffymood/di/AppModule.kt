package com.imp.fluffymood.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppModule: Application() {

    override fun onCreate() {
        super.onCreate()

        initializeService()
    }

    /**
     * 서비스 설정
     */
    private fun initializeService() {

        applicationContext?.let {

        }
    }
}