package com.imp.data.repository

import android.annotation.SuppressLint
import com.imp.data.mapper.CommonMapper
import com.imp.data.remote.api.ApiLog
import com.imp.data.util.ApiClient
import com.imp.data.util.extension.isSuccess
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.model.LogModel
import com.imp.domain.repository.LogRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Home - Log Repository Implementation
 */
class LogRepositoryImpl @Inject constructor() : LogRepository {

    /**
     * Load Log Data
     */
    @SuppressLint("CheckResult")
    override suspend fun loadLogData(id: String, date: String, successCallback: (LogModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        ApiClient.getClient().create(ApiLog::class.java).logData(id, date)
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
}