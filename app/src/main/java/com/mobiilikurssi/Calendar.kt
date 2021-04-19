package com.mobiilikurssi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

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
}