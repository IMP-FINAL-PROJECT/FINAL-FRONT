package com.imp.domain.repository

import com.imp.domain.model.SensorModel

/**
 * Main - Home Repository Interface
 */
interface HomeRepository {

    /**
     * 센싱 데이터 저장
     */
    suspend fun saveSensorData(data: SensorModel, successCallback: () -> Unit, errorCallback: (String?) -> Unit)
}