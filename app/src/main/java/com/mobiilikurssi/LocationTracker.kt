package com.mobiilikurssi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import android.location.Location
import android.location.LocationListener
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class LocationTracker(private val ctx : Context) : LocationListener {
    private var locationManager : LocationManager? = null
    private var permissionGranted : Boolean = false

    private val locations : MutableList <Pair<Location,Long>> = ArrayList()
    private var startTime : Long = 0
    private var totalDistance = 0.0f;

    @SuppressLint("MissingPermission")
    override fun onLocationChanged(location : Location) {
        val newTime = System.currentTimeMillis()

        if(locations.count() >= 1) {
            val locationDelta = location.distanceTo(locations.last().first)
            totalDistance += locationDelta;
        }

        //  Add the location and call the user callback
        locations.add(Pair(location, newTime))
        onNewLocation?.invoke(totalDistance / 1000)
    }

    fun forEachLocation(callback : (location : Location, timeDiff : Long) -> Unit) {
        var lastTime = startTime

        for(entry in locations) {
            callback.invoke(entry.first, entry.second - lastTime)
            lastTime = entry.second
        }
    }

    var onNewLocation : ((totalKilometers : Float) -> Unit)? = null

    init {
        locationManager = ctx.getSystemService(LOCATION_SERVICE) as LocationManager?

        permissionGranted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            Log.i("test", "Request perms");
            ActivityCompat.requestPermissions(ctx as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
        }
    }

    @SuppressLint("MissingPermission")
    fun track(enabled : Boolean) {
        if(enabled) {
            if (permissionGranted) {
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1.5f, this);
                startTime = System.currentTimeMillis()
                Log.i("test", "Request location");
            }
        }

        else {
            Log.i("test", "Stop Request location");
            locationManager?.removeUpdates(this)
        }
    }

    fun getLastLocation() : String {
        val location = locations.last().first
        return "${location.latitude} : ${location.longitude}"
    }

    fun getLat() : Double {
       return locations.last().first.latitude
    }

    fun getLong() : Double {
        return locations.last().first.longitude
    }

    fun getTotalDistance() : Float {
        return totalDistance
    }
}