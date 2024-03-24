package com.imp.domain.repository

import com.imp.domain.model.ChatListModel

/**
 * Main - Chat Repository Interface
 */
interface ChatRepository {

    /**
     * 채팅 리스트
     */
    suspend fun chatList(successCallback: (ArrayList<ChatListModel>) -> Unit, errorCallback: (String?) -> Unit)
}