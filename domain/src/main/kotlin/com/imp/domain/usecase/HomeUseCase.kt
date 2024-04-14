package com.imp.domain.usecase

import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.model.HomeModel
import com.imp.domain.model.SensorModel
import com.imp.domain.repository.HomeRepository
import javax.inject.Inject


/**
 * Main - Home UseCase
 */
class HomeUseCase @Inject constructor(private val repository: HomeRepository) {

    /**
     * 홈 데이터
     */
    suspend fun homeData(id: String, successCallback: (HomeModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.homeData(id, successCallback, errorCallback)
    }
    /**
     * Save Mood
     */
    suspend fun saveMood(id: String, mood: Int, successCallback: (Boolean) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.saveMood(id, mood, successCallback, errorCallback)
    }

    /**
     * 센싱 데이터 저장
     */
    suspend fun saveSensorData(data: SensorModel, successCallback: () -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.saveSensorData(data, successCallback, errorCallback)
    }
}