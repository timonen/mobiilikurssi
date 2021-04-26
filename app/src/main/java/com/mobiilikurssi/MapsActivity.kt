package com.mobiilikurssi

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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

        var b : TextView = findViewById(R.id.button_start)
        b.setOnClickListener {
            tracker.toggleTrack()
            Toast.makeText(applicationContext,"Matkan mittaaminen aloitettu", Toast.LENGTH_SHORT).show()

            //  Käytä tracker.getStatus()
        }

        tracker.onNewLocation = { km ->
            val t : TextView = findViewById(R.id.textView)
            t.text = "total $km km ${tracker.getLastLocation()}"
        }

        findViewById<Button>(R.id.button_settings).setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }
        findViewById<Button>(R.id.button_history).setOnClickListener {
            startActivity(Intent(this, Calendar::class.java))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
    }
}