package com.imp.domain.repository

import com.imp.domain.model.ChatListModel
import com.imp.domain.model.ErrorCallbackModel

/**
 * Main - Chat Repository Interface
 */
interface ChatRepository {

    /**
     * 채팅 리스트
     */
    suspend fun chatList(id: String, successCallback: (ArrayList<ChatListModel.Chat>) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)

    /**
     * 채팅 생성
     */
    suspend fun createChat(id: String, successCallback: (String) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)

    /**
     * 채팅 삭제
     */
    suspend fun deleteChat(id: String, number: String, successCallback: () -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)
}