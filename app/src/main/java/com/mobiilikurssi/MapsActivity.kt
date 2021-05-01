package com.mobiilikurssi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

/**
 * TODO write docs
 * Map activity class
 * @author
 * @version 1.0
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val tracker by lazy { LocationTracker(this) }

    private var lastLocation : Location? = null
    private var lines : MutableList <Polyline> = ArrayList()

    /**
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)

        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun toggleStartButton() {
        //  Find the start button and invert the state
        val startButton : TextView = findViewById(R.id.button_start)
        val state = !startButton.isClickable

        //  Set opacity
        when(state) {
            false -> startButton.alpha = 0.5f
            true -> startButton.alpha = 1.0f
        }

        //  Toggle button clickable state
        startButton.isClickable = state
    }

    /**
     * @param googleMap
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true

        var startButton : TextView = findViewById(R.id.button_start)
        startButton.setOnClickListener {
            toggleStartButton()
            tracker.toggleTrack()
        }

        tracker.onStartTracking = {
            //  Clear previous route
            for(line in lines)
                line.remove()

            //  Reset the line origin
            lastLocation = null

            toggleStartButton()
            val btn = findViewById<Button>(R.id.button_start)
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.mapred))
            btn.text = "Lopeta seuranta"
        }

        tracker.onEndTracking = {
            toggleStartButton()
            val btn = findViewById<Button>(R.id.button_start)
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.themegreen))
            btn.text = "Aloita liikkuminen"

            setIntentCalendar(true)
        }

        tracker.onNewLocation = { count ->
            val t : TextView = findViewById(R.id.textView)
            val currentLocation = tracker.getLastLocation()

            if(lastLocation != null) {
                //  Connect the locations with lines
                val prev : Location = lastLocation as Location
                val opt = PolylineOptions().add(LatLng(prev.latitude, prev.longitude),
                        LatLng(currentLocation.latitude, currentLocation.longitude))
                        .width(5f)
                        .color(Color.RED)

                lines.add(googleMap.addPolyline(opt))
            }

            t.text = "$count locations"
            lastLocation = currentLocation

            val position = LatLng(currentLocation.latitude, currentLocation.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17.0f));
        }

        findViewById<Button>(R.id.button_settings).setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }

        findViewById<Button>(R.id.button_history).setOnClickListener {
            setIntentCalendar(false)
        }
    }

    /**
     * This function sets Intent for Calendar.
     * @param update Boolean that handles updating value
     *
     */
    private fun setIntentCalendar(update: Boolean) {
        val pref: SharedPreferences = this.getSharedPreferences("SETTINGS", MODE_PRIVATE)
        val weight = pref.getString("weight", "empty")

        val intent = Intent(this, Calendar::class.java).apply {
            putExtra("totalkm", tracker.getTotalKilometers())
            putExtra("addProgression", update)
            if(weight != null) {
                val avgS = (tracker.getTotalMeters().div(tracker.getDurationSeconds())).div(3.6)
                if(weight != "empty") {
                    putExtra("totalkcal", getTotalCalories(tracker.getDurationMinutes(), avgS, weight.toInt()))
                } else
                    putExtra("weightset", false)
            }
        }
        startActivity(intent)
    }

    /**
     * This function calculates total calories
     * Since this is not an medical application we feel comfortable using formula:
     * Total calories burned = Duration (in minutes)*(MET*3.5*weight in kg)/200
     *
     * @see <a href="https://www.verywellfit.com/how-many-calories-you-burn-during-exercise-4111064">
     *     https://www.verywellfit.com/how-many-calories-you-burn-during-exercise</a>
     *
     * MET (metabolic equivalent for task)
     * Formula: 1 km/h average speed = 1.1 MET
     * @see <a href="https://metscalculator.com/">https://metscalculator.com</a>
     *
     * @param duration
     * @param avgSpeed
     * @param weight
     * @return Float
     */
    private fun getTotalCalories(duration: Double, avgSpeed: Double, weight: Int) : Float {
        return ((duration * ((avgSpeed * 1.1) * 3.5 * weight)).div(200)).toFloat()
    }
}