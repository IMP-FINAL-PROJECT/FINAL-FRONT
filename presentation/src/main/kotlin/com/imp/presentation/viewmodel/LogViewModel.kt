package com.imp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.model.LogModel
import com.imp.domain.usecase.LogUseCase
import com.imp.presentation.widget.utils.CommonUtil
import com.imp.presentation.widget.utils.Event
import com.kakao.vectormap.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sign

/**
 * Main - Log ViewModel
 */
@HiltViewModel
class LogViewModel @Inject constructor(private val useCase: LogUseCase) : ViewModel() {

    /** log data */
    private var _logData: MutableLiveData<LogModel> = MutableLiveData()
    val logData: LiveData<LogModel> get() = _logData

    /** location point list */
    private var _pointList: MutableLiveData<ArrayList<LatLng>> = MutableLiveData()
    val pointList: LiveData<ArrayList<LatLng>> get() = _pointList

    /** Error Callback */
    private val _errorCallback = MutableLiveData<Event<ErrorCallbackModel?>>()
    val errorCallback: LiveData<Event<ErrorCallbackModel?>> get() = _errorCallback

    /**
     * Load Log Data
     */
    fun loadData(id: String) = viewModelScope.launch {

        useCase.loadLogData(
            id = id,
            successCallback = {
                _logData.value = it
                setPointList()
            },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * Set Point List (ArrayList<Double> -> MapPoint)
     */
    private fun setPointList() = viewModelScope.launch {

        val list = ArrayList<LatLng>()
        _logData.value?.gps?.forEach { location ->

            if (location.size > 2) {
                list.add(LatLng.from(location[0], location[1]))
            }
        }

        _pointList.value = list
    }
}