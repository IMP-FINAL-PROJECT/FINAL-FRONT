package com.imp.data.repository

import android.annotation.SuppressLint
import com.imp.data.mapper.CommonMapper
import com.imp.data.remote.api.ApiChat
import com.imp.data.util.ApiClient
import com.imp.data.util.extension.isSuccess
import com.imp.domain.model.ChatListModel
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.repository.ChatRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Main - Chat Repository Implementation
 */
@SuppressLint("CheckResult")
class ChatRepositoryImpl @Inject constructor() : ChatRepository {

    override suspend fun chatList(id: String, successCallback: (ArrayList<ChatListModel.Chat>) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        ApiClient.getChatClient().create(ApiChat::class.java).chatList(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    response.data?.let { successCallback.invoke(it.chatList) }
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }

    override suspend fun createChat(id: String, successCallback: (ChatListModel.Chat) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        val params: MutableMap<String, Any> = HashMap()

        params["id"] = id

        ApiClient.getChatClient().create(ApiChat::class.java).createChat(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    response.data?.let { successCallback.invoke(it) }
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }

    override suspend fun deleteChat(id: String, number: String, successCallback: () -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        val params: MutableMap<String, Any> = HashMap()

        params["id"] = id
        params["number"] = number

        ApiClient.getChatClient().create(ApiChat::class.java).deleteChat(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    successCallback.invoke()
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }
}