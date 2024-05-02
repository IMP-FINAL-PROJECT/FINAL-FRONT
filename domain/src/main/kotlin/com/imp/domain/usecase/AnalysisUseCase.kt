package com.imp.domain.usecase

import com.imp.domain.model.AnalysisModel
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.repository.AnalysisRepository
import javax.inject.Inject

/**
 * Main - Analysis UseCase
 */
class AnalysisUseCase @Inject constructor(private val repository: AnalysisRepository) {

    /**
     * Load Analysis Data
     */
    suspend fun loadAnalysisData(id: String, date: String, successCallback: (AnalysisModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.loadAnalysisData(id, date, successCallback, errorCallback)
    }
}