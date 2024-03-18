package com.imp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.domain.model.LogModel
import com.imp.domain.usecase.LogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPoint
import javax.inject.Inject

/**
 * Main - Log ViewModel
 */
@HiltViewModel
class LogViewModel @Inject constructor(private val useCase: LogUseCase) : ViewModel() {

    /** log data */
    private var _logData: MutableLiveData<LogModel> = MutableLiveData()
    val logData: LiveData<LogModel> get() = _logData

    /** location point list */
    private var _pointList: MutableLiveData<ArrayList<MapPoint>> = MutableLiveData()
    val pointList: LiveData<ArrayList<MapPoint>> get() = _pointList

    /**
     * Load Log Data
     */
    fun loadData() = viewModelScope.launch {
        _logData.value = useCase.loadLogData()

        // set point list
        setPointList()
    }

    /**
     * Set Point List (ArrayList<Double> -> MapPoint)
     */
    private fun setPointList() = viewModelScope.launch {

        val list = ArrayList<MapPoint>()
        _logData.value?.location?.valueList?.forEach {

            if (it.size >= 2) {
                list.add(MapPoint.mapPointWithGeoCoord(it[0], it[1]))
            }
        }

        _pointList.value = list
    }
}