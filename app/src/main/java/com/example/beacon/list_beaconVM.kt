package com.example.beacon

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.altbeacon.beacon.Beacon

class list_beaconVM : ViewModel() {
    val listBeacon = mutableStateOf(emptyList<Beacon>())
}