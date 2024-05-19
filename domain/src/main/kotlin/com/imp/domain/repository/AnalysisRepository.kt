package com.imp.domain.repository

import com.imp.domain.model.AddressModel
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

    /**
     * Coordinate to Region Code
     */
    suspend fun coordinateToRegionCode(x: String, y: String, successCallback: (AddressModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)
}