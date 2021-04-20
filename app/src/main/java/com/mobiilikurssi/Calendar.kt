package com.mobiilikurssi

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class Calendar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // text gives short description on how far the user is with their goals(%)
        val goals = findViewById<TextView>(R.id.textview_goals)

        findViewById<Button>(R.id.button_goals).setOnClickListener {
            startActivity(Intent(this, Goal::class.java))

        }
    }

    override fun onResume() {
        super.onResume()

        val pref: SharedPreferences = this.getSharedPreferences("GOAL", MODE_PRIVATE)

        val getTime = pref.getString(R.array.time.toString(), "empty")
        val getUnit = pref.getString(R.array.format.toString(), "empty")
        val getAmount = pref.getString("amount", "1")
        Log.d("main", getTime.toString())
        Log.d("main", getUnit.toString())
        Log.d("main", getAmount.toString())

    }


}