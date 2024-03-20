package com.imp.fluffymood.di

import com.imp.domain.repository.HomeRepository
import com.imp.domain.repository.LogRepository
import com.imp.domain.usecase.HomeUseCase
import com.imp.domain.usecase.LogUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    @Singleton
    fun provideHomeUseCase(repository: HomeRepository) = HomeUseCase(repository)

    @Provides
    @Singleton
    fun provideLogUseCase(repository: LogRepository) = LogUseCase(repository)
}