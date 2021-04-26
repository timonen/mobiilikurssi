package com.mobiilikurssi

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import kotlin.math.pow

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_settings)

        val height = findViewById<EditText>(R.id.text_height)
        val weight = findViewById<EditText>(R.id.text_weight)
        val bmiText = findViewById<TextView>(R.id.textView_bmi)

        // find preferences
        val pref: SharedPreferences = this.getSharedPreferences("SETTINGS", MODE_PRIVATE)
        pref.getString("height", "empty")?.let { Log.d("main", it) }
        pref.getString("weight", "empty")?.let { Log.d("main", it) }
        pref.getString("bmi", "empty")?.let { Log.d("main", it) }


        findViewById<Button>(R.id.create_button).setOnClickListener {
            val editor = pref.edit()
            editor.putString("height", height.text.toString())
            editor.putString("weight", weight.text.toString())

            val h = height.text.toString().toDouble()
            val w = weight.text.toString().toDouble()
            val bmi = "%.1f".format(h.div(100).let { w.div(it.pow(2)) })
            val realbmi = h.div(100).let { w.div(it.pow(2)) }
            editor.putString("bmi", bmi)
            editor.apply()

            bmiText.text = bmiSettings(realbmi)

            Toast.makeText(applicationContext,"Profiilin tiedot tallennettu",Toast.LENGTH_SHORT).show()
        }
    }

    private fun bmiSettings(bmi: Double): String {
        return when(bmi) {
            in 0.0..18.5 -> "Olet alipainoinen"
            in 18.6..24.9 -> "Olet normaalipainoinen"
            in 25.0..29.0 -> "Olet ylipainoinen"
            else -> "Olet liikalihava"
        }
    }


}
