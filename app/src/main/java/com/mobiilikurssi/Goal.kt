package com.mobiilikurssi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*

class Goal : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_goal)

        val timelayout = findViewById<LinearLayout>(R.id.time_layout)

        Spinner(R.array.time, R.layout.activity_goal, timelayout)
        Log.d("main", "toimii")


    }
}


