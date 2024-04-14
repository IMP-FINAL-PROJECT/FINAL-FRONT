package com.imp.data.remote.api

import com.imp.data.util.BaseResponse
import com.imp.data.util.HttpConstants
import com.imp.domain.model.HomeModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

@JvmSuppressWildcards
interface ApiHome {

    /**
     * 홈 데이터
     */
    @Headers("Content-Type: application/json")
    @GET(HttpConstants.API_HOME)
    fun homeData(
        @Query("id") id: String
    ): Observable<BaseResponse<HomeModel>>

    /**
     * Save Mood
     */
    @Headers("Content-Type: application/json")
    @POST(HttpConstants.API_MOOD_INSERT)
    fun saveMood(
        @Body body: Map<String, Any>
    ): Observable<BaseResponse<Any>>

    /**
     * 센싱 데이터 저장
     */
    @Headers("Content-Type: application/json")
    @POST(HttpConstants.API_SENSOR_INSERT)
    fun sensorData(
        @Body body: Map<String, Any>
    ): Observable<BaseResponse<Any>>
}