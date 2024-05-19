package com.imp.domain.usecase

import com.imp.domain.model.AddressModel
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

    /**
     * Coordinate to Region Code
     */
    suspend fun coordinateToRegionCode(x: String, y: String, successCallback: (AddressModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.coordinateToRegionCode(x, y, successCallback, errorCallback)
    }
}