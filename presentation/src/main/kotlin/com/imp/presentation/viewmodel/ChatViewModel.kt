package com.imp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.domain.model.ChatListModel
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.usecase.ChatUseCase
import com.imp.presentation.widget.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main - Chat ViewModel
 */
@HiltViewModel
class ChatViewModel @Inject constructor(private val useCase: ChatUseCase) : ViewModel() {

    /** Chatting List */

    private var _chatList: MutableLiveData<ArrayList<ChatListModel.Chat>> = MutableLiveData()
    val chatList: LiveData<ArrayList<ChatListModel.Chat>> get() = _chatList

    /** Chatting Response */

    private var _chatResponse: MutableLiveData<ChatListModel.ChatResponse> = MutableLiveData()
    val chatResponse: LiveData<ChatListModel.ChatResponse> get() = _chatResponse

    /** Chat Callback */

    private var _chatCallback: MutableLiveData<ChatListModel.Chat> = MutableLiveData()
    val chatCallback: LiveData<ChatListModel.Chat> get() = _chatCallback

    /** Error Callback */
    private var _errorCallback: MutableLiveData<Event<ErrorCallbackModel?>> = MutableLiveData()
    val errorCallback: LiveData<Event<ErrorCallbackModel?>> get() = _errorCallback

    /**
     * 채팅 리스트
     */
    fun chatList(id: String) = viewModelScope.launch {

        useCase.chatList(
            id = id,
            successCallback = { _chatList.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * 채팅 생성
     */
    fun createChat(id: String) = viewModelScope.launch {

        useCase.createChat(
            id = id,
            successCallback = { _chatCallback.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * 채팅 삭제
     */
    fun deleteChat(id: String, number: String) = viewModelScope.launch {

        useCase.deleteChat(
            id = id,
            number = number,
            successCallback = { chatList(id) },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * 채팅 전송
     */
    fun sendChat(id: String, number: String, request: String) = viewModelScope.launch {

        useCase.sendChat(
            id = id,
            number = number,
            request = request,
            successCallback = { _chatResponse.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * Reset Data
     */
    fun resetData() {

        _chatList = MutableLiveData()
        _chatResponse = MutableLiveData()
        _chatCallback = MutableLiveData()
        _errorCallback = MutableLiveData()
    }
}