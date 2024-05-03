package com.imp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.domain.model.AddressModel
import com.imp.domain.model.ErrorCallbackModel
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

    /** Register Response Data */
    private val _registerResData = MutableLiveData<MemberModel>()
    val registerResData: LiveData<MemberModel> get() = _registerResData

    /** Email Validation Data */
    private val _emailValidationData = MutableLiveData<Boolean>()
    val emailValidationData: LiveData<Boolean> get() = _emailValidationData

    /** Address Data */
    private val _addressData = MutableLiveData<AddressModel>()
    val addressData: LiveData<AddressModel> get() = _addressData

    /** Member Data */
    private val _memberData = MutableLiveData<MemberModel>()
    val memberData: LiveData<MemberModel> get() = _memberData

    /** Error Callback */
    private val _errorCallback = MutableLiveData<Event<ErrorCallbackModel?>>()
    val errorCallback: LiveData<Event<ErrorCallbackModel?>> get() = _errorCallback

    /** Register Data */
    val registerData: MemberModel = MemberModel()

    /**
     * Login
     */
    fun login(id: String, password: String, pushToken: String) = viewModelScope.launch {

        useCase.login(
            id = id,
            password = password,
            token = pushToken,
            successCallback = { _loginData.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * register
     */
    fun register() = viewModelScope.launch {

        useCase.register(
            data = registerData,
            successCallback = { _registerResData.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * Check Email Validation
     */
    fun checkEmail(id: String) = viewModelScope.launch {

        useCase.checkEmail(
            id = id,
            successCallback = { _emailValidationData.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * Search Address
     */
    fun searchAddress(search: String) = viewModelScope.launch {

        useCase.searchAddress(
            search = search,
            successCallback = { _addressData.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * Get Member Data
     */
    fun getMember() = viewModelScope.launch {

        useCase.getMemberData(
            successCallback = { _memberData.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * Edit Profile
     */
    fun editProfile(id: String, name: String, birth: String, gender: String) = viewModelScope.launch {

        useCase.editProfile(
            data = MemberModel(
                id = id,
                name = name,
                birth = birth,
                gender = gender,
            ),
            successCallback = { _loginData.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }
}