package com.imp.domain.repository

import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.model.LogModel

/**
 * Main - Log Repository Interface
 */
interface LogRepository {

    /**
     * Load Log Data
     */
    suspend fun loadLogData(id: String, date: String, successCallback: (LogModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)
}