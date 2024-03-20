package com.imp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    /** Error Callback */
    private val _errorCallback = MutableLiveData<Event<String?>>()
    val errorCallback: LiveData<Event<String?>> get() = _errorCallback

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