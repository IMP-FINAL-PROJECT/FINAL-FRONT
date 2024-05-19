package com.imp.data.remote.api

import com.imp.data.util.BaseResponse
import com.imp.data.util.HttpConstants
import com.imp.domain.model.AddressModel
import com.imp.domain.model.AnalysisModel
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

@JvmSuppressWildcards
interface ApiAnalysis {

    /**
     * Analysis Data
     */
    @Headers("Content-Type: application/json")
    @GET(HttpConstants.API_ANALYSIS)
    fun analysisData(
        @Query("id") id: String,
        @Query("date") date: String
    ): Observable<BaseResponse<AnalysisModel>>

    /**
     * Coordinate to Region Code
     */
    @Headers("Content-Type: application/json")
    @GET(HttpConstants.API_KAKAO_ADDRESS_REGION_CODE)
    fun regionCode(
        @Header("Authorization") key: String,
        @Query("x") x: String,
        @Query("y") y: String
    ): Call<AddressModel>
}