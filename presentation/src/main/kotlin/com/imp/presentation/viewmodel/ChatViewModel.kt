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

    private val _chatList = MutableLiveData<ArrayList<ChatListModel.Chat>>()
    val chatList: LiveData<ArrayList<ChatListModel.Chat>> get() = _chatList

    /** Chat Callback */

    private val _chatCallback = MutableLiveData<String>()
    val chatCallback: LiveData<String> get() = _chatCallback

    /** Error Callback */
    private val _errorCallback = MutableLiveData<Event<ErrorCallbackModel?>>()
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
}