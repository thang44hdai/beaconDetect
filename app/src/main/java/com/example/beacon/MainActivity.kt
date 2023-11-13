package com.example.beacon

import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.example.beacon.ui.theme.BeaconTheme
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.MonitorNotifier
import kotlin.properties.Delegates

class MainActivity : ComponentActivity() {
    lateinit var beaconReference: BeaconReference
    var alertDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BeaconTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    home()
                    beaconReference = application as BeaconReference
                    val regionViewModel = BeaconManager.getInstanceForApplication(this)
                        .getRegionViewModel(beaconReference.region)
                    regionViewModel.regionState.observe(this, monitoringObserver)
                    regionViewModel.rangedBeacons.observe(this, rangingObserver)
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    val monitoringObserver = Observer<Int> { state ->
        var dialogTitle = "Beacons detected"
        var dialogMessage = "didEnterRegionEvent has fired"
        var stateString = "inside"
        if (state == MonitorNotifier.OUTSIDE) {
            dialogTitle = "No beacons detected"
            dialogMessage = "didExitRegionEvent has fired"
            stateString = "outside"
//            beaconCountTextView.text = "Outside of the beacon region -- no beacons detected"
//            beaconListView.adapter = ArrayAdapter(this, R.layout.simple_list_item_1, arrayOf("--"))
        } else {
//            beaconCountTextView.text = "Inside the beacon region."
        }
        Log.d(TAG, "Monitoring state changed to : $stateString")
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.setPositiveButton(R.string.ok, null)
        alertDialog?.dismiss()
        alertDialog = builder.create()
        alertDialog?.show()
    }

    val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(TAG, "Ranged: ${beacons.count()} beacons")
        if (BeaconManager.getInstanceForApplication(this).rangedRegions.size > 0) {
            for (beacon in beacons) {
                Toast.makeText(this, beacon.toString(), Toast.LENGTH_SHORT).show()
            }
        } else Toast.makeText(this, "No Beacons Detected", Toast.LENGTH_SHORT).show()

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun home() {
        var monitoringButton by rememberSaveable {
            mutableStateOf("Start Monitoring")
        }
        var rangingButton by rememberSaveable {
            mutableStateOf("Start Ranging")
        }
        var list_beacon by rememberSaveable { mutableStateOf(emptyList<Int>()) }


        fun monitoringButtonTapped() {
            var dialogTitle = ""
            var dialogMessage = ""
            val beaconManager = BeaconManager.getInstanceForApplication(this)
            if (beaconManager.monitoredRegions.size == 0) {
                beaconManager.startMonitoring(beaconReference.region)
                dialogTitle = "Beacon monitoring started."
                dialogMessage =
                    "You will see a dialog if a beacon is detected, and another if beacons then stop being detected."
                monitoringButton = "Stop Monitoring"
            } else {
                beaconManager.stopMonitoring(beaconReference.region)
                dialogTitle = "Beacon monitoring stopped."
                dialogMessage =
                    "You will no longer see dialogs when beacons start/stop being detected."
                monitoringButton = "Start Monitoring"
            }
            val builder =
                AlertDialog.Builder(this)
            builder.setTitle(dialogTitle)
            builder.setMessage(dialogMessage)
            builder.setPositiveButton(android.R.string.ok, null)
            alertDialog?.dismiss()
            alertDialog = builder.create()
            alertDialog?.show()
        }

        fun rangingButtonTapped() {
            val beaconManager = BeaconManager.getInstanceForApplication(this)

            if (beaconManager.rangedRegions.size == 0) {
                beaconManager.startRangingBeacons(beaconReference.region)
                rangingButton = "Stop Ranging"
            } else {
                beaconManager.stopRangingBeacons(beaconReference.region)
                rangingButton = "Start Ranging"
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detect Beacons App") },
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(onClick = {
                        list_beacon += listOf<Int>(9)
                        rangingButtonTapped();
                    }) {
                        Text(text = rangingButton)
                    }
                    Button(onClick = { monitoringButtonTapped() }) {
                        Text(text = monitoringButton)
                    }

                }
            }
        ) { it ->
            Column(modifier = Modifier.padding(it)) {
                Text(
                    //text = "Detected ${list_beacon.list_beacon.value?.size} beacon(s)",
                    text = "Detected ${list_beacon.size} beacon(s)",
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                LazyColumn() {
                    items(list_beacon) { it ->
                        Text(text = it.toString())
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        BeaconTheme {
            home()
        }
    }

    companion object {
        val TAG = "MainActivity"
        val PERMISSION_REQUEST_BACKGROUND_LOCATION = 0
        val PERMISSION_REQUEST_BLUETOOTH_SCAN = 1
        val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 2
        val PERMISSION_REQUEST_FINE_LOCATION = 3
    }
}

