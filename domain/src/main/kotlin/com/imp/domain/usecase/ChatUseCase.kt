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
    suspend fun createChat(id: String, successCallback: (String) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.createChat(id, successCallback, errorCallback)
    }

    /**
     * 채팅 삭제
     */
    suspend fun deleteChat(id: String, number: String, successCallback: () -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.deleteChat(id, number, successCallback, errorCallback)
    }
}