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

    override fun onLocationChanged(location : Location) {
    }

    init {
        locationManager = ctx.getSystemService(LOCATION_SERVICE) as LocationManager?

        permissionGranted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            Log.i("test", "Request perms");
            ActivityCompat.requestPermissions(ctx as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
        }
    }

    @SuppressLint("MissingPermission")
    fun track() {
        if(permissionGranted) {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1f, this);
            Log.i("test", "Request location");
        }
    }

    @SuppressLint("MissingPermission")
    fun getCoordinates() : String {
        if(permissionGranted) {
            var l: Location? = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return "${l?.latitude} : ${l?.longitude}"
        }

        return "No permission for location"
    }
}