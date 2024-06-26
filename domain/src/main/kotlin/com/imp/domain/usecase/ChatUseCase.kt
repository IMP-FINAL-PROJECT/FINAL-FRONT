package com.imp.domain.usecase

import com.imp.domain.model.ChatListModel
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Main - Chat UseCase
 */
class ChatUseCase @Inject constructor(private val repository: ChatRepository) {

    /**
     * 채팅 리스트
     */
    suspend fun chatList(id: String, successCallback: (ArrayList<ChatListModel.Chat>) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.chatList(id, successCallback, errorCallback)
    }

    /**
     * 채팅 생성
     */
    suspend fun createChat(id: String, successCallback: (ChatListModel.Chat) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.createChat(id, successCallback, errorCallback)
    }

    /**
     * 채팅 삭제
     */
    suspend fun deleteChat(id: String, number: String, successCallback: () -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.deleteChat(id, number, successCallback, errorCallback)
    }

    /**
     * 채팅 전송
     */
    suspend fun sendChat(id: String, number: String, request: String, successCallback: (ChatListModel.ChatResponse) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.sendChat(id, number, request, successCallback, errorCallback)
    }
}