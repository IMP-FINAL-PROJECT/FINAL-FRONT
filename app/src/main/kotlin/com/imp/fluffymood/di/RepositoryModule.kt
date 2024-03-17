package com.imp.fluffymood.di

import com.imp.data.repository.LogRepositoryImpl
import com.imp.domain.repository.LogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideLogRepository(): LogRepository = LogRepositoryImpl()
}