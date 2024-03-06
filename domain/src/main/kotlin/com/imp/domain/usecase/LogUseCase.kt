package com.imp.domain.usecase

import com.imp.domain.repository.LogRepository
import javax.inject.Inject


/**
 * Main - Log UseCase
 */
class LogUseCase @Inject constructor(private val repository: LogRepository) {

    /**
     * Load Log Data
     */
    suspend fun loadLogData() = repository.loadLogData()
}