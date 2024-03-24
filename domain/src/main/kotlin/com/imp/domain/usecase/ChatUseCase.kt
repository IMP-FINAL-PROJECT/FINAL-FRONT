package com.imp.domain.usecase

import com.imp.domain.model.ChatListModel
import com.imp.domain.repository.ChatRepository
import javax.inject.Inject


/**
 * Main - Chat UseCase
 */
class ChatUseCase @Inject constructor(private val repository: ChatRepository) {

    /**
     * 채팅 리스트
     */
    suspend fun chatList(successCallback: (ArrayList<ChatListModel>) -> Unit, errorCallback: (String?) -> Unit) {
        repository.chatList(successCallback, errorCallback)
    }
}