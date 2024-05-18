package com.imp.data.remote.api

import com.imp.data.util.BaseResponse
import com.imp.data.util.HttpConstants
import com.imp.domain.model.ChatListModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

@JvmSuppressWildcards
interface ApiChat {

    /**
     * 채팅 목록
     */
    @Headers("Content-Type: application/json")
    @GET(HttpConstants.API_CHAT_LIST)
    fun chatList(
        @Query("id") id: String
    ): Observable<BaseResponse<ChatListModel>>

    /**
     * 채팅 생성
     */
    @Headers("Content-Type: application/json")
    @POST(HttpConstants.API_CHAT_CREATE)
    fun createChat(
        @Body body: Map<String, Any>
    ): Observable<BaseResponse<ChatListModel.Chat>>

    /**
     * 채팅 삭제
     */
    @Headers("Content-Type: application/json")
    @POST(HttpConstants.API_CHAT_DELETE)
    fun deleteChat(
        @Body body: Map<String, Any>
    ): Observable<BaseResponse<Any>>

    /**
     * 채팅 전송
     */
    @Headers("Content-Type: application/json")
    @POST(HttpConstants.API_CHAT_QUESTION)
    fun sendChat(
        @Body body: Map<String, Any>
    ): Observable<BaseResponse<ChatListModel.ChatResponse>>
}