package com.dst.lineproject.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val deviceSet = HashSet<DeviceModel>()
    private var _deviceModel = MutableLiveData<Set<DeviceModel>>()
    val deviceModel : LiveData<Set<DeviceModel>>
    get() = _deviceModel
    fun pushDevice(deviceModel: DeviceModel){
        deviceSet.add(deviceModel)
        _deviceModel.value = deviceSet
    }
}