package com.imp.data.repository

import android.annotation.SuppressLint
import com.imp.data.BuildConfig
import com.imp.data.mapper.CommonMapper
import com.imp.data.remote.api.ApiAnalysis
import com.imp.data.util.ApiClient
import com.imp.data.util.HttpConstants
import com.imp.data.util.extension.isSuccess
import com.imp.domain.model.AddressModel
import com.imp.domain.model.AnalysisModel
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.repository.AnalysisRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    /**
     * Coordinate to Region Code
     */
    @SuppressLint("CheckResult")
    override suspend fun coordinateToRegionCode(x: String, y: String, successCallback: (AddressModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        Retrofit.Builder()
            .baseUrl(HttpConstants.KAKAO_BASE_HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiAnalysis::class.java)
            .regionCode(key = BuildConfig.KAKAO_REST_API_KEY, x = y, y = x)
            .enqueue(object: Callback<AddressModel> {
                override fun onResponse(call: Call<AddressModel>, response: Response<AddressModel>) {

                    response.body()?.let { successCallback.invoke(it) }
                }
                override fun onFailure(call: Call<AddressModel>, t: Throwable) {

                    errorCallback.invoke(CommonMapper.mappingErrorData(t))
                }
            })
    }
}