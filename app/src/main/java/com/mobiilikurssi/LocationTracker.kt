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
    private var tracking = false;

    @SuppressLint("MissingPermission")
    override fun onLocationChanged(location : Location) {
        if(locations.count() == 0) {
            onStartTracking?.invoke()
            startTime = System.currentTimeMillis()
        }

        val newTime = System.currentTimeMillis()

        if(locations.count() >= 1) {
            var locationDelta = location.distanceTo(locations.last().first)
            totalDistance += locationDelta;
        }

        //  Add the location and call the user callback
        locations.add(Pair(location, newTime))
        onNewLocation?.invoke(locations.count())
    }

    fun forEachLocation(callback : (location : Location, timeDiff : Long) -> Unit) {
        var lastTime = startTime

        for(entry in locations) {
            callback.invoke(entry.first, entry.second - lastTime)
            lastTime = entry.second
        }
    }

    var onNewLocation : ((locationCount : Int) -> Unit)? = null
    var onStartTracking : (() -> Unit)? = null
    var onEndTracking : (() -> Unit)? = null

    init {
        locationManager = ctx.getSystemService(LOCATION_SERVICE) as LocationManager?

        permissionGranted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            Log.i("test", "Request perms");
            ActivityCompat.requestPermissions(ctx as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
        }
    }

    @SuppressLint("MissingPermission")
    fun toggleTrack() {
        //  Toggle from true -> false or false -> true
        tracking = !tracking

        if(tracking) {
            if (permissionGranted) {
                locations.clear()
                totalDistance = 0.0f

                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5f, this);
            }
        }

        else {
            locationManager?.removeUpdates(this)
            onEndTracking?.invoke()
        }
    }

    fun getLastLocation() : Location = locations.last().first

    fun getDurationSeconds() : Long {
        if(!tracking)
            return 0

        val newTime = System.currentTimeMillis()
        return (newTime - startTime) / 1000
    }

    fun getDurationMinutes() : Double = getDurationSeconds().toDouble() / 60

    fun getTotalMeters() = totalDistance
    fun getTotalKilometers() = totalDistance / 1000
}