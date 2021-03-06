package com.chkan.firstproject.ui.result_map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chkan.domain.models.ResultModel
import com.chkan.base.utils.ResultType
import com.chkan.base.utils.Result
import com.chkan.domain.usecases.result_map.GetPolylineForRouteUseCase
import com.chkan.domain.usecases.result_map.SaveSelectedPlaceUseCase
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val getPolylineForRouteUseCase : GetPolylineForRouteUseCase,
    private val saveSelectedPlaceUseCase : SaveSelectedPlaceUseCase
) : ViewModel(){

    private val _polylineLiveData = MutableLiveData<PolylineOptions>()
    val polylineLiveData: LiveData<PolylineOptions>
        get() = _polylineLiveData

    private val isErrorMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isErrorLiveData: LiveData<Boolean>
        get() = isErrorMutableLiveData

    fun getRout(latLngStart: String, latLngFinish: String) {
        viewModelScope.launch {
            val result = getPolylineForRouteUseCase.getPolylineForRoute(latLngStart,latLngFinish)
            updatePolylineLiveData(result)
        }
    }

    private fun updatePolylineLiveData(result: Result<PolylineOptions>) {
        if (result.resultType== ResultType.SUCCESS && result.data !=null) {
            _polylineLiveData.value = result.data!!
        } else {
            onResultError()
        }
    }

    private fun onResultError() {
        viewModelScope.launch {
            delay(300)
        }.invokeOnCompletion {
            isErrorMutableLiveData.value = true
        }
    }

    fun saveLatLng(resultModel : ResultModel) {
        saveSelectedPlaceUseCase.savePlace(resultModel)
    }

}