package com.imp.data.repository

import android.annotation.SuppressLint
import com.imp.data.mapper.CommonMapper
import com.imp.data.remote.api.ApiAnalysis
import com.imp.data.util.ApiClient
import com.imp.data.util.extension.isSuccess
import com.imp.domain.model.AnalysisModel
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.repository.AnalysisRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Main - Analysis Repository Implementation
 */
class AnalysisRepositoryImpl @Inject constructor() : AnalysisRepository {

    /**
     * Load Analysis Data
     */
    @SuppressLint("CheckResult")
    override suspend fun loadAnalysisData(id: String, date: String, successCallback: (AnalysisModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        ApiClient.getClient().create(ApiAnalysis::class.java).analysisData(id, date)
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