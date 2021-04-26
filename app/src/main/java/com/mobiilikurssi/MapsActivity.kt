package com.mobiilikurssi

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val tracker by lazy { LocationTracker(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)

        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        var startButton : TextView = findViewById(R.id.button_start)
        startButton.setOnClickListener {
            tracker.toggleTrack()
        }

        tracker.onStartTracking = {
            Toast.makeText(applicationContext, "Matkan seuraaminen aloitettu", Toast.LENGTH_SHORT).show()
            var toggleTrack : TextView = findViewById(R.id.button_start)
            toggleTrack.text = "Lopeta seuranta"
        }

        tracker.onEndTracking = {
            Toast.makeText(applicationContext, "Matkan seuraaminen lopetettu", Toast.LENGTH_SHORT).show()
            var toggleTrack : TextView = findViewById(R.id.button_start)
            toggleTrack.text = "Aloita liikkuminen"

            var processed = 0
            var previousLocation : Location? = null
            var timeSum : Long = 0

            tracker.forEachLocation { location, timeDiff ->
                if(processed > 0) {
                    val prev : Location = previousLocation as Location

                    //  Connect the locations with lines
                    val line: Polyline = googleMap.addPolyline(PolylineOptions()
                            .add(LatLng(prev.latitude, prev.longitude), LatLng(location.latitude, location.longitude))
                            .width(5f)
                            .color(Color.RED))
                }

                previousLocation = location
                timeSum += timeDiff
                processed++
            }

            //  Get average speed by calculating distance / time
            val averageSpeedMS : Float = tracker.getTotalMeters() / (timeSum / 1000)
            Toast.makeText(applicationContext, "Keskinopeus $averageSpeedMS", Toast.LENGTH_SHORT).show()
        }

        tracker.onNewLocation = {
            val t : TextView = findViewById(R.id.textView)
            t.text = "total ${tracker.getTotalKilometers()} km ${tracker.getLastLocation()}"
        }

        findViewById<Button>(R.id.button_settings).setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }
        findViewById<Button>(R.id.button_history).setOnClickListener {
            val intent = Intent(this, Calendar::class.java).apply {
                putExtra("totalkm", tracker.getTotalKilometers())
            }
            startActivity(intent)
        }
    }
}