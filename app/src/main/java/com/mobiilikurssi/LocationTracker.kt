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
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class LocationTracker(private val ctx : Context) : LocationListener {
    private var locationManager : LocationManager? = null
    private var permissionGranted : Boolean = false

    val handler = Handler(Looper.getMainLooper())
    var tracking = false

    private val locations : MutableList <Location> = ArrayList()
    private var locationTime : Long = 0

    @SuppressLint("MissingPermission")
    override fun onLocationChanged(location : Location) {
        val newTime = System.currentTimeMillis()
        var speed  = 0.0f

        //  Calculate the speed and distance if there's at least 2 locations
        if(locations.count() >= 2) {
            val timeDelta = newTime - locationTime
            val locationDelta = location.distanceTo(locations.last())

            speed = locationDelta / (timeDelta / 1000)
        }

        //  Add the location and call the user callback
        locations.add(location)
        onNewLocation?.invoke(speed)
    }

    var onNewLocation : ((speed : Float) -> Unit)? = null
    var onLocationTimeout : (() -> Unit)? = null

    init {
        locationManager = ctx.getSystemService(LOCATION_SERVICE) as LocationManager?

        permissionGranted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            Log.i("test", "Request perms");
            ActivityCompat.requestPermissions(ctx as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
        }
    }

    private fun checkTimeout() {
        val timeoutMs = 3000
        val newTime = System.currentTimeMillis()
        val difference = newTime - locationTime

        Log.i("test","Diff $difference")

        if(difference >= timeoutMs)
            onLocationTimeout?.invoke()
    }

    @SuppressLint("MissingPermission")
    fun track(enabled : Boolean) {
        if(enabled) {
            if (permissionGranted) {
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1f, this);
                Log.i("test", "Request location");
                locationTime = System.currentTimeMillis()
                tracking = true

                handler.post(object : Runnable {
                    override fun run() {
                        if(!tracking)
                            return

                        checkTimeout()
                        handler.postDelayed(this, 1000)
                    }
                })
            }
        }

        else {
            Log.i("test", "Stop Request location");
            locationManager?.removeUpdates(this)
            tracking = false
        }
    }

    fun getLastLocation() : String {
        val location = locations.last()
        return "${location.latitude} : ${location.longitude}"
    }
}