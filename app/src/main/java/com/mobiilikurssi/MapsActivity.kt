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
            tracker.track(true)
            Toast.makeText(applicationContext,"Matkan mittaaminen aloitettu", Toast.LENGTH_SHORT).show()
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val location = LocationTracker(this)

        //TODO get location!
            try {
                val lat = location.getLat()
                val long = location.getLong()
                val sydney = LatLng(lat, long)
                mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                // why is this exception happening
            } catch (e : NoSuchElementException){
                val sydney = LatLng(-34.0, 151.0)
                mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
            }


        }
}