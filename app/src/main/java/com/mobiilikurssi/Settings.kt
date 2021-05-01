package com.mobiilikurssi

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import kotlin.math.pow

/**
 * Settings
 *
 * This class takes users height and weight as an input.
 * Outputs users bmi back to the user.
 * And saves all the values as preferences.
 *
 * @author Valtteri Viirret
 * @version 1.0
 */
class Settings : AppCompatActivity() {
    /**
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_settings)

        val height = findViewById<EditText>(R.id.text_height)
        val weight = findViewById<EditText>(R.id.text_weight)
        val bmiText = findViewById<TextView>(R.id.textView_bmi)
        val userMeasure = findViewById<TextView>(R.id.textView_stats)

        val pref: SharedPreferences = this.getSharedPreferences("SETTINGS", MODE_PRIVATE)

        // show weight and height
        if(pref.getBoolean("measureSet", false)) {
            userMeasure.text = "Pituutesi: ${pref.getString("height", "e")}cm / Painosi: ${pref.getString("weight", "e")}kg"
        }

        findViewById<Button>(R.id.create_button).setOnClickListener {
            val editor = pref.edit()
            editor.putString("height", height.text.toString())
            editor.putString("weight", weight.text.toString())
            editor.putBoolean("measureSet", true)

            val h = height.text.toString().toDouble()
            val w = weight.text.toString().toDouble()

            // bmi and bmi formatted
            val bmi = h.div(100).let { w.div(it.pow(2)) }
            val bmiF = "%.1f".format(h.div(100).let { w.div(it.pow(2)) })

            editor.putString("bmi", bmiF)
            editor.apply()

            bmiText.text = bmiSettings(bmi)

            Toast.makeText(applicationContext,"Profiilin tiedot tallennettu",Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * @param bmi
     * @return a description of weight based on bmi
     */
    private fun bmiSettings(bmi: Double): String {
        return when(bmi) {
            in 0.0..18.5 -> "Olet alipainoinen"
            in 18.5..24.9 -> "Olet normaalipainoinen"
            in 24.9..29.0 -> "Olet ylipainoinen"
            else -> "Olet liikalihava"
        }
    }
}
