package com.imp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.model.HomeModel
import com.imp.domain.model.SensorModel
import com.imp.domain.usecase.HomeUseCase
import com.imp.presentation.widget.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main - Home ViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val useCase: HomeUseCase) : ViewModel() {

    /** home data */
    private var _homeData: MutableLiveData<HomeModel> = MutableLiveData()
    val homeData: LiveData<HomeModel> get() = _homeData

    /** Error Callback */
    private val _errorCallback = MutableLiveData<Event<ErrorCallbackModel?>>()
    val errorCallback: LiveData<Event<ErrorCallbackModel?>> get() = _errorCallback

    /**
     * 홈 데이터
     */
    fun homeData(id: String) = viewModelScope.launch {

        useCase.homeData(
            id = id,
            successCallback = { _homeData.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * Save Mood
     */
    fun saveMood(id: String, mood: Int) = viewModelScope.launch {

        useCase.saveMood(
            id = id,
            mood = mood,
            successCallback = {  },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * Load Log Data
     */
    fun saveSensorData(data: SensorModel) = viewModelScope.launch {

        useCase.saveSensorData(
            data = data,
            successCallback = {  },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }
}