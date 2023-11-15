package com.example.beacon

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.Region

class MyForegroundService() : Service() {
    lateinit var beaconReference: BeaconReference
    var region = Region("all-beacons", null, null, null)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setupBeaconScanning()
        return super.onStartCommand(intent, flags, startId);
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        var beaconManager = BeaconManager.getInstanceForApplication(this)
        var parser = BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        parser.setHardwareAssistManufacturerCodes(arrayOf(0x004c).toIntArray())
        beaconManager.beaconParsers.add(parser)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setupBeaconScanning() {
        beaconReference = application as BeaconReference

        var beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.startRangingBeacons(region)

        val regionViewModel =
            BeaconManager.getInstanceForApplication(this).getRegionViewModel(region)
        regionViewModel.rangedBeacons.observeForever(rangingObserver)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(MainActivity.TAG, "Ranged: ${beacons.count()} beacons")
        if (BeaconManager.getInstanceForApplication(this).rangedRegions.size > 0) {
            sendNotification("Phát hiện ${beacons.size} beacons")
        } else {
            sendNotification("No Beacons Detected")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(content: String) {
        val builder = NotificationCompat.Builder(this, "beacon-ref-notification-id")
            .setContentTitle("Beacon Detected App")
            .setContentText(content)
            .setSmallIcon(com.example.beacon.R.drawable.logo)
        val channel = NotificationChannel(
            "beacon-ref-notification-id",
            "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.setDescription("My Notification Channel Description")
        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId(channel.getId());
        notificationManager.notify(1, builder.build())
    }

}