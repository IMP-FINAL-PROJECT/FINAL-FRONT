package com.imp.domain.usecase

import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.model.LogModel
import com.imp.domain.repository.LogRepository
import javax.inject.Inject

/**
 * Home - Log UseCase
 */
class LogUseCase @Inject constructor(private val repository: LogRepository) {

    /**
     * Load Log Data
     */
    suspend fun loadLogData(id: String, date: String, successCallback: (LogModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.loadLogData(id, date, successCallback, errorCallback)
    }
}