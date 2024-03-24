package com.imp.data.repository

import android.annotation.SuppressLint
import com.imp.domain.model.ChatListModel
import com.imp.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Main - Chat Repository Implementation
 */
class ChatRepositoryImpl @Inject constructor() : ChatRepository {

    @SuppressLint("CheckResult")
    override suspend fun chatList(successCallback: (ArrayList<ChatListModel>) -> Unit, errorCallback: (String?) -> Unit) {

        val dummyList = ArrayList<ChatListModel>()

        for(i in 0 until 10) {
            dummyList.add(ChatListModel(
                name = "착하지만 바보같은 동욱봇 $i",
                chat = "ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ",
                time = System.currentTimeMillis(),
                isRead = i % 2 == 0
            ))
        }

        successCallback.invoke(dummyList)
    }
}