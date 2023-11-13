package com.example.beacon

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import org.altbeacon.beacon.Beacon

class list_beaconVM : ViewModel() {
    private val _list_beacon = MutableLiveData(ArrayList<Beacon>())
    val list_beacon: LiveData<ArrayList<Beacon>> = _list_beacon

    fun update(a: ArrayList<Beacon>) {
        _list_beacon.value?.clear()
        for (beacon in a) {
            _list_beacon.value?.add(beacon)
        }
    }

}