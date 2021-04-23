package com.mobiilikurssi

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*

class Settings : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_settings)

        val height = findViewById<EditText>(R.id.text_height)
        val weight = findViewById<EditText>(R.id.text_weight)

        val pref: SharedPreferences = this.getSharedPreferences("SETTINGS", MODE_PRIVATE)
        val editor = pref.edit()

        findViewById<Button>(R.id.create_button).setOnClickListener {
            editor.putString("height", height.text.toString())
            editor.putString("weight", weight.text.toString())
            editor.apply()
            Toast.makeText(applicationContext,"Profiilin tiedot tallennettu",Toast.LENGTH_SHORT).show()
        }

        pref.getString("height", "empty")?.let { Log.d("main", it) }
        pref.getString("weight", "empty")?.let { Log.d("main", it) }

    }


}
