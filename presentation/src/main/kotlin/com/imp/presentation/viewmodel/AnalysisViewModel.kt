package com.imp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.domain.model.AnalysisModel
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.usecase.AnalysisUseCase
import com.imp.presentation.widget.utils.Event
import com.kakao.vectormap.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main - Analysis ViewModel
 */
@HiltViewModel
class AnalysisViewModel @Inject constructor(private val useCase: AnalysisUseCase) : ViewModel() {

    /** analysis data */
    private var _analysisData: MutableLiveData<AnalysisModel> = MutableLiveData()
    val analysisData: LiveData<AnalysisModel> get() = _analysisData

    /** location point list */
    private var _pointList: MutableLiveData<ArrayList<LatLng>> = MutableLiveData()
    val pointList: LiveData<ArrayList<LatLng>> get() = _pointList

    /** Error Callback */
    private val _errorCallback = MutableLiveData<Event<ErrorCallbackModel?>>()
    val errorCallback: LiveData<Event<ErrorCallbackModel?>> get() = _errorCallback

    /**
     * Load Analysis Data
     */
    fun loadData(id: String, date: String) = viewModelScope.launch {

        useCase.loadAnalysisData(
            id = id,
            date = date,
            successCallback = {
                _analysisData.value = it
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
        _analysisData.value?.place_diversity?.forEach { location ->

            // todo 임시로 3개 제한
            if (list.size >= 3) return@forEach

            if (location.size >= 3) {
                list.add(LatLng.from(location[1], location[2]))
            }
        }

        _pointList.value = list
    }
}