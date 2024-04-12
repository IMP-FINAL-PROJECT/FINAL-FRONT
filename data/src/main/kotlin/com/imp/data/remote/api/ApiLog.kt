package com.imp.data.remote.api

import com.imp.data.util.BaseResponse
import com.imp.data.util.HttpConstants
import com.imp.domain.model.LogModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

@JvmSuppressWildcards
interface ApiLog {

    /**
     * Log Data
     */
    @Headers("Content-Type: application/json")
    @POST(HttpConstants.API_ANALYSIS)
    fun logData(
        @Query("id") id: String
    ): Observable<BaseResponse<LogModel>>
}