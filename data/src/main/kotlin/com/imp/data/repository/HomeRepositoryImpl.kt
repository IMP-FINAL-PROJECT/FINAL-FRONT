package com.imp.data.repository

import android.annotation.SuppressLint
import com.imp.data.mapper.CommonMapper
import com.imp.data.remote.api.ApiHome
import com.imp.data.util.ApiClient
import com.imp.data.util.extension.isSuccess
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.model.HomeModel
import com.imp.domain.model.SensorModel
import com.imp.domain.repository.HomeRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Main - Log Repository Implementation
 */
class HomeRepositoryImpl @Inject constructor() : HomeRepository {

    /**
     * 홈 데이터
     */
    @SuppressLint("CheckResult")
    override suspend fun homeData(id: String, successCallback: (HomeModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        ApiClient.getClient().create(ApiHome::class.java).homeData(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    response.data?.let { successCallback.invoke(it) }
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }

    /**
     * 센싱 데이터 저장
     */
    @SuppressLint("CheckResult")
    override suspend fun saveSensorData(data: SensorModel, successCallback: () -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        val params: MutableMap<String, Any> = HashMap()

        params["id"] = data.id ?: ""
        params["illuminance"] = data.illuminance
        params["pedometer"] = data.pedometer
        params["screen_frequency"] = data.screenFrequency
        params["screen_duration"] = data.screenDuration
        params["gps"] = data.gps
        params["timestamp"] = data.timestamp ?: 0
        params["hour"] = data.hour

        ApiClient.getClient().create(ApiHome::class.java).sensorData(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    successCallback.invoke()
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->

                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }
}