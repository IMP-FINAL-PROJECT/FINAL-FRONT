package com.imp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.domain.model.LogModel
import com.imp.domain.usecase.LogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main - Log ViewModel
 */
@HiltViewModel
class LogViewModel @Inject constructor(private val useCase: LogUseCase) : ViewModel() {

    private var _logData: MutableLiveData<LogModel> = MutableLiveData()
    val logData: LiveData<LogModel> get() = _logData

    fun loadData() = viewModelScope.launch {
        _logData.value = useCase.loadLogData()
    }
}