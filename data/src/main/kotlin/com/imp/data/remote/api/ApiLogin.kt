package com.imp.data.remote.api

import com.imp.data.util.BaseResponse
import com.imp.data.util.HttpConstants
import com.imp.domain.model.MemberModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

@JvmSuppressWildcards
interface ApiLogin {

    /**
     * Login
     */
    @Headers("Content-Type: application/json")
    @POST(HttpConstants.API_LOGIN)
    fun login(
        @Body body: Map<String, Any>
    ): Observable<BaseResponse<MemberModel>>
}