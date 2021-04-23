package com.mobiilikurssi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class Calendar : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val goals = findViewById<TextView>(R.id.textview_goals)
        val completed = findViewById<TextView>(R.id.textView_completed)

        findViewById<Button>(R.id.button_goals).setOnClickListener {
            startActivity(Intent(this, Goal::class.java))
        }

        val pref: SharedPreferences = this.getSharedPreferences("GOAL", MODE_PRIVATE)

        val getTime = pref.getString("päivä", "empty")
        val getUnit = pref.getString("kalori", "empty")
        val getAmount = pref.getString("amount", "1")

        var gU = ""
        when(getUnit) {
            "kalori" -> gU = "kaloria"
            "kilometri" -> gU = "kilometriä"
            "kilogramma" -> gU = "kilogrammaa"
        }

        goals.text = "Tavoite: $getAmount $gU / $getTime"

       val amount = LocationTracker(this)

        when(getUnit) {
            "kilometri" -> completed.text = "Tavoitteesta suoritettu ${amount.getTotalDistance()} km / $getAmount km"
            "kilogramma" -> completed.text = "Not coded yet"
            "kalori" -> completed.text = "Not coded yet"
        }

    }
}