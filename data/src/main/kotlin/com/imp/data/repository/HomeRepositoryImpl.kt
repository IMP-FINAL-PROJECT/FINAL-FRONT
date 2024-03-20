package com.imp.data.repository

import android.annotation.SuppressLint
import com.imp.data.remote.api.ApiHome
import com.imp.data.util.ApiClient
import com.imp.data.util.extension.isSuccess
import com.imp.domain.model.SensorModel
import com.imp.domain.repository.HomeRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Main - Log Repository Implementation
 */
class HomeRepositoryImpl @Inject constructor() : HomeRepository {

    @SuppressLint("CheckResult")
    override suspend fun saveSensorData(data: SensorModel, successCallback: () -> Unit, errorCallback: (String?) -> Unit) {

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
                    errorCallback.invoke(response.message)
                }

            }, { error ->

                errorCallback.invoke(error.message)
            })
    }
}