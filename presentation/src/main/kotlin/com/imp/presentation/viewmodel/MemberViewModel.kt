package com.imp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.domain.model.MemberModel
import com.imp.domain.usecase.MemberUseCase
import com.imp.presentation.widget.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Member ViewModel
 */
@HiltViewModel
class MemberViewModel @Inject constructor(private val useCase: MemberUseCase) : ViewModel() {

    /** Login Data */
    private val _loginData = MutableLiveData<MemberModel>()
    val loginData: LiveData<MemberModel> get() = _loginData

    /** Error Callback */
    private val _errorCallback = MutableLiveData<Event<String?>>()
    val errorCallback: LiveData<Event<String?>> get() = _errorCallback

    /**
     * Login
     */
    fun login(id: String, password: String) = viewModelScope.launch {

        useCase.login(
            id = id,
            password = password,
            successCallback = { _loginData.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }
}