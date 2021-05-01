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

/**
 * TODO write docs
 * Location tracked class
 * @author
 * @version 1.0
 */
class LocationTracker(private val ctx : Context) : LocationListener {
    private var locationManager : LocationManager? = null
    private var permissionGranted : Boolean = false

    private val locations : MutableList <Pair<Location,Long>> = ArrayList()

    private var startTime : Long = 0
    private var elapsedTime : Long = 0

    private var totalDistance = 0.0f;
    private var tracking = false;

    /**
     * Since LocationTracker inherits from LocationListener, onLocationChanged
     * is required. Whenever the GPS detects a new location, this function is called
     * @param location The new location
     */
    @SuppressLint("MissingPermission")
    override fun onLocationChanged(location : Location) {
        //  If this is the first location, do preparation
        if(locations.count() == 0) {
            onStartTracking?.invoke()
            startTime = System.currentTimeMillis()
        }

        //  Track the elapsed time
        val currentTime = System.currentTimeMillis()
        elapsedTime = currentTime - startTime

        //  When there's at least 1 location, add to the total distance
        if(locations.count() >= 1) {
            var locationDelta = location.distanceTo(locations.last().first)
            totalDistance += locationDelta;
        }

        //  Add the location and call the user callback
        locations.add(Pair(location, currentTime))
        onNewLocation?.invoke(locations.count())
    }

    /**
     * This function exists to eliminate the need for count and
     * location at index getters. It's used to perform some
     * action for each location
     * @param callback What happens for each location
     */
    fun forEachLocation(callback : (location : Location, timeDiff : Long) -> Unit) {
        var lastTime = startTime

        //  Call the callback for each location
        for(entry in locations) {
            callback.invoke(entry.first, entry.second - lastTime)
            lastTime = entry.second
        }
    }

    /**
     *  onNewLocation is a callback that's called when there's a new location.
     */
    var onNewLocation : ((locationCount : Int) -> Unit)? = null

    /**
     *  onStartTracking is a callback that's called when tracking has started. */
    var onStartTracking : (() -> Unit)? = null

    /**
     *  onEndTracking is a callback that's called when tracking has ended. */
    var onEndTracking : (() -> Unit)? = null

    init {
        //  Get the location manager service
        locationManager = ctx.getSystemService(LOCATION_SERVICE) as LocationManager?

        //  Make sure that we have permissions
        permissionGranted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            Log.i("test", "Request perms");
            ActivityCompat.requestPermissions(ctx as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
        }
    }

    /**
     *  This function toggles the state of the tracker
     */
    @SuppressLint("MissingPermission")
    fun toggleTrack() {
        //  Toggle from true -> false or false -> true
        tracking = !tracking

        //  If the tracker should track and we have permissions, reset and request location
        if(tracking) {
            if (permissionGranted) {
                locations.clear()
                totalDistance = 0.0f
                elapsedTime = 0

                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5f, this);
            }
        }

        //  If the tracker shouldn't track stop tracking
        else {
            locationManager?.removeUpdates(this)
            onEndTracking?.invoke()
        }
    }

    /**
     * @return The most recent location
     */
    fun getLastLocation() : Location = locations.last().first

    /**
     * @return Duration since tracker start in seconds. Returns 0 if tracker is disabled
     */
    fun getDurationSeconds() = elapsedTime / 1000

    /**
     * @return Duration since tracker start in minutes. Returns 0 if tracker is disabled
     */
    fun getDurationMinutes() : Double = getDurationSeconds().toDouble() / 60

    /**
     * @return Total meters tracked.
     */
    fun getTotalMeters() = totalDistance

    /**
     * @return Total kilometers tracked
     */
    fun getTotalKilometers() = totalDistance / 1000
}