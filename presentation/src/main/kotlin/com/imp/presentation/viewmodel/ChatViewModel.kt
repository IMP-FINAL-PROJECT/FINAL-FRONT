package com.imp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.domain.model.ChatListModel
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

    private val _chatList = MutableLiveData<ArrayList<ChatListModel>>()
    val chatList: LiveData<ArrayList<ChatListModel>> get() = _chatList

    /** Error Callback */
    private val _errorCallback = MutableLiveData<Event<String?>>()
    val errorCallback: LiveData<Event<String?>> get() = _errorCallback

    /**
     * 채팅 리스트
     */
    fun chatList() = viewModelScope.launch {

        useCase.chatList(
            successCallback = { _chatList.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }
}