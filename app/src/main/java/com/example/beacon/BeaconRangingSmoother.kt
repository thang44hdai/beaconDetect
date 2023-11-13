package com.example.beacon

import org.altbeacon.beacon.Beacon

class BeaconRangingSmoother {
    var beacons: ArrayList<Beacon> = ArrayList<Beacon>()
    var smoothingWindowMillis: Long = 10000
    var visibleBeacons: List<Beacon> = ArrayList<Beacon>()
        get() {
            var visible = ArrayList<Beacon>()
            for (beacon in beacons) {
                if (System.currentTimeMillis() - beacon.lastCycleDetectionTimestamp < smoothingWindowMillis) {
                    visible.add(beacon)
                }
            }
            return visible
        }

    fun add(detectedBeacons: Collection<Beacon>): BeaconRangingSmoother {
        for (beacon in detectedBeacons) {
            beacon.lastCycleDetectionTimestamp = System.currentTimeMillis()
            beacons.add(beacon)
        }
        return this
    }

    companion object {
        var TAG = "BeaconRangingSmoother"
        var shared = BeaconRangingSmoother()
    }

}