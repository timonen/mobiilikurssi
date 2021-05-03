package com.mobiilikurssi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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
 *  The main menu class containing all the necessary buttons and a map
 * @author Roope Rekunen, Valtteri Viirret
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private val tracker by lazy { LocationTracker(this) }

    private var lastLocation : Location? = null
    private var lines : MutableList <Polyline> = ArrayList()

    /**
     * onCreate is a function that's called when the activity is being created
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
     * Function that's called when the google map is ready
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

        tracker.onLocationUnavailable = {
            toggleStartButton()
            Toast.makeText(this, "Laita GPS päälle ja varmista että sovellus saa etsiä sijaintisi", Toast.LENGTH_LONG).show()
        }

        //  What happens when tracking has started?
        tracker.onStartTracking = {
            //  Clear previous route
            for(line in lines)
                line.remove()

            //  Reset the line origin
            lastLocation = null

            //  The start button should now be the stop button
            toggleStartButton()
            val btn = findViewById<Button>(R.id.button_start)
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.mapred))
            btn.text = "Lopeta seuranta"
        }

        //  What happens when tracking has ended?
        tracker.onEndTracking = {
            //  The stop button should now be the start button
            toggleStartButton()
            val btn = findViewById<Button>(R.id.button_start)
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.themegreen))
            btn.text = "Aloita liikkuminen"

            //  Show the calendar and save the progression
            startTarget(true)
        }

        //  What happens when there's a new location
        tracker.onNewLocation = { count ->
            val currentLocation = tracker.getLastLocation()
            Log.i("main", "$count locations")

            if(lastLocation != null) {
                //  Connect the locations with lines
                val prev : Location = lastLocation as Location
                val opt = PolylineOptions().add(LatLng(prev.latitude, prev.longitude),
                        LatLng(currentLocation.latitude, currentLocation.longitude))
                        .width(5f)
                        .color(Color.RED)

                //  Save the line and show it
                lines.add(googleMap.addPolyline(opt))
            }

            lastLocation = currentLocation

            //  Move the camera to the new position
            val position = LatLng(currentLocation.latitude, currentLocation.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17.0f));
        }

        //  Go to the settings menu when the button is pressed
        findViewById<Button>(R.id.button_settings).setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }

        findViewById<Button>(R.id.button_goals).setOnClickListener {
            startTarget(false)
        }

        findViewById<Button>(R.id.button_history).setOnClickListener {
            startActivity(Intent(this, History::class.java))
        }
    }

    /**
     * This function sets Intent for Target.
     * @param update Boolean that handles updating value
     *
     */
    private fun startTarget(update: Boolean) {
        // get user weight
        val pref: SharedPreferences = this.getSharedPreferences("SETTINGS", MODE_PRIVATE)
        val weight = pref.getString("weight", "empty")

        val intent = Intent(this, Target::class.java).apply {
            putExtra("totalkm", tracker.getTotalKilometers())

            // parameter for calling this function. True means updating values
            putExtra("addProgression", update)

            if(weight != null) {
                // average speed for trip. (m/s) / 3.6 = km/h
                val avgS = (tracker.getTotalMeters().div(tracker.getDurationSeconds())).div(3.6)

                // send calorie information if weight is set
                if(weight != "empty") {

                    // getTotalCalories(duration, average speed, user weight)
                    putExtra("totalkcal", getTotalCalories(tracker.getDurationMinutes(), avgS, weight.toInt()))
                } else
                    // boolean checking if weight is set by user
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