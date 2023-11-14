package com.example.beacon

import android.app.NotificationChannel
import android.app.NotificationManager
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

class MyForegroundService(context: Context) : Service() {
    lateinit var beaconReference: BeaconReference

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        beaconReference = applicationContext as BeaconReference
        val beaconManager = BeaconManager.getInstanceForApplication(this)
        val regionViewModel = BeaconManager.getInstanceForApplication(applicationContext)
            .getRegionViewModel(beaconReference.region)
        beaconManager.startRangingBeacons(beaconReference.region)
        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(MainActivity.TAG, "Ranged: ${beacons.count()} beacons")
        if (BeaconManager.getInstanceForApplication(applicationContext).rangedRegions.size > 0) {
            sendNotification("Phát hiện ${beacons.size} beacons")
        } else Toast.makeText(applicationContext, "No Beacons Detected", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(content: String) {
        val builder = NotificationCompat.Builder(applicationContext, "beacon-ref-notification-id")
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

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}