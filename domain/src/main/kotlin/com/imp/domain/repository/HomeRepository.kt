package com.imp.domain.repository

import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.model.HomeModel
import com.imp.domain.model.RecommendModel
import com.imp.domain.model.SensorModel

/**
 * Main - Home Repository Interface
 */
interface HomeRepository {

    /**
     * 홈 데이터
     */
    suspend fun homeData(id: String, successCallback: (HomeModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)

    /**
     * Save Mood
     */
    suspend fun saveMood(id: String, mood: Int, successCallback: (Boolean) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)

    /**
     * 센싱 데이터 저장
     */
    suspend fun saveSensorData(data: SensorModel, successCallback: () -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)

    /**
     * Recommend Data
     */
    suspend fun recommendData(id: String, score: Int, successCallback: (ArrayList<RecommendModel>) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)
}