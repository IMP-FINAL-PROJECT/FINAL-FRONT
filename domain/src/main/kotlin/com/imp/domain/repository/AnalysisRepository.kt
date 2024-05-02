package com.imp.domain.repository

import com.imp.domain.model.AnalysisModel
import com.imp.domain.model.ErrorCallbackModel

/**
 * Main - Analysis Repository Interface
 */
interface AnalysisRepository {

    /**
     * Load Analysis Data
     */
    suspend fun loadAnalysisData(id: String, date: String, successCallback: (AnalysisModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)
}