package com.imp.data.remote.api

import com.imp.data.util.BaseResponse
import com.imp.data.util.HttpConstants
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

@JvmSuppressWildcards
interface ApiHome {

    /**
     * 센싱 데이터 저장
     */
    @Headers("Content-Type: application/json")
    @POST(HttpConstants.API_SENSOR_INSERT)
    fun sensorData(
        @Body body: Map<String, Any>
    ): Observable<BaseResponse<Any>>
}