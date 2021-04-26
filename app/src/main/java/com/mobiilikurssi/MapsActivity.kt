package com.mobiilikurssi
/*
FORMULA FOR CALORIECOUNTER:
https://www.verywellfit.com/how-many-calories-you-burn-during-exercise-4111064
Total calories burned = Duration (in minutes)*(MET*3.5*weight in kg)/200

MET (metabolic equivalent for task)
Since this is not an medical application we feel comfortable
using formula: 1 km/h = 1.1 MET
https://metscalculator.com/
 */
import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
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
            val btn = findViewById<Button>(R.id.button_start)
            var toggleTrack : TextView = btn
            btn.setBackgroundColor(resources.getColor(R.color.chili))
            toggleTrack.text = "Lopeta seuranta"
        }

        tracker.onEndTracking = {
            Toast.makeText(applicationContext, "Matkan seuraaminen lopetettu", Toast.LENGTH_SHORT).show()
            val btn = findViewById<Button>(R.id.button_start)
            var toggleTrack : TextView = btn
            btn.setBackgroundColor(resources.getColor(R.color.themegreen))
            toggleTrack.text = "Aloita liikkuminen"

            var processed = 0
            var previousLocation : Location? = null
            var timeSum : Long = 0

            val opt = PolylineOptions()
            tracker.forEachLocation { location, timeDiff ->
                if(processed > 0) {
                    val prev : Location = previousLocation as Location

                    //  Connect the locations with lines
                    opt.add(LatLng(prev.latitude, prev.longitude),
                            LatLng(location.latitude, location.longitude))
                            .width(5f)
                            .color(Color.RED)
                }

                googleMap.addPolyline(opt)

                previousLocation = location
                timeSum += timeDiff
                processed++
            }

            //  Get average speed by calculating distance / time
            val averageSpeedMS : Float = tracker.getTotalMeters() / (timeSum / 1000)
            Toast.makeText(applicationContext, "Avg $averageSpeedMS Processed $processed", Toast.LENGTH_SHORT).show()
        }

        tracker.onNewLocation = {
            val t : TextView = findViewById(R.id.textView)
            t.text = "total ${tracker.getTotalMeters()} km ${tracker.getLastLocation()}"
        }

        findViewById<Button>(R.id.button_settings).setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }
        findViewById<Button>(R.id.button_history).setOnClickListener {
            val intent = Intent(this, Calendar::class.java).apply {
                putExtra("totalkm", tracker.getTotalKilometers())
                // here add values for duration and avgSpeed parameters
                // these are just for testing
                putExtra("totalkcal", getTotalCalories(30, 5.0))
            }
            startActivity(intent)
        }
    }

    private fun getTotalCalories(duration : Int, avgSpeed : Double) : Double {
        val pref: SharedPreferences = this.getSharedPreferences("SETTINGS", MODE_PRIVATE)
        val weight = pref.getString("weight", "empty")?.toInt()!!
        return duration * ((avgSpeed * 1.1) * 3.5 * weight).div(200)
    }
}