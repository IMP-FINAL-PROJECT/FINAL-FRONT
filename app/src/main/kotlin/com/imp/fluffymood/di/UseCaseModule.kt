package com.imp.fluffymood.di

import com.imp.domain.repository.LogRepository
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
    fun provideLogUseCase(repository: LogRepository) = LogUseCase(repository)
}