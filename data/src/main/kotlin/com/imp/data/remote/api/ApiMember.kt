package com.imp.data.remote.api

import com.imp.data.util.BaseResponse
import com.imp.data.util.HttpConstants
import com.imp.domain.model.AddressModel
import com.imp.domain.model.MemberModel
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

@JvmSuppressWildcards
interface ApiMember {

    /**
     * Login
     */
    @Headers("Content-Type: application/json")
    @POST(HttpConstants.API_LOGIN)
    fun login(
        @Body body: Map<String, Any>
    ): Observable<BaseResponse<MemberModel>>

    /**
     * Register
     */
    @Headers("Content-Type: application/json")
    @POST(HttpConstants.API_REGISTER)
    fun register(
        @Body body: Map<String, Any>
    ): Observable<BaseResponse<MemberModel>>

    /**
     * Check Email Validation
     */
    @Headers("Content-Type: application/json")
    @GET(HttpConstants.API_REGISTER_VALIDATION)
    fun checkEmail(
        @Query("id") id: String
    ): Observable<BaseResponse<Any>>

    /**
     * Kakao 주소 검색
     */
    @Headers("Content-Type: application/json")
    @GET(HttpConstants.API_KAKAO_ADDRESS_SEARCH)
    fun search(
        @Header("Authorization") key: String,
        @Query("query") query: String,
        @Query("size") size: Int = 30
    ): Call<AddressModel>

    /**
     * Edit Profile
     */
    @Headers("Content-Type: application/json")
    @PUT(HttpConstants.API_CHANGE)
    fun editProfile(
        @Path("id", encoded = true) id: String,
        @Body body: Map<String, Any>
    ): Observable<BaseResponse<MemberModel>>
}