package com.imp.domain.usecase

import com.imp.domain.model.SensorModel
import com.imp.domain.repository.HomeRepository
import javax.inject.Inject


/**
 * Main - Home UseCase
 */
class HomeUseCase @Inject constructor(private val repository: HomeRepository) {

    /**
     * 센싱 데이터 저장
     */
    suspend fun saveSensorData(data: SensorModel, successCallback: () -> Unit, errorCallback: (String?) -> Unit) {
        repository.saveSensorData(data, successCallback, errorCallback)
    }
}