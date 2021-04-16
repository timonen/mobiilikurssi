package com.mobiilikurssi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton

class Settings : AppCompatActivity() {

    // either "kcal" or "kJ" depending user preferences
    var energyUnit = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_settings)

        // use these lines in order to get preferences
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        energyUnit = sharedPreferences.getString("energy", energyUnit).toString()
        Log.d("main", energyUnit)

        // tallenna checkkaus booleanina preferensseihin
        // when lauseella izi pizi

        findViewById<RadioButton>(R.id.radio_kcal).setOnClickListener() {
            energyUnit = "kcal"
        }
        findViewById<RadioButton>(R.id.radio_kj).setOnClickListener() {
            energyUnit = "kJ"
        }


    }


    override fun onPause() {
        super.onPause()

        val sharedPreference = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("energy", energyUnit)
        editor.apply()
    }
}