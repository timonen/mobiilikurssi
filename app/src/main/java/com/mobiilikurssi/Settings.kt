package com.mobiilikurssi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup

class Settings : AppCompatActivity() {

    // variable that controls energy preference
    var energyUnit = "kcal"


    /**
     * Whole class depricated deleting soon
     */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_settings)

        // use these lines in order to get preferences
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        energyUnit = sharedPreferences.getString("energy", energyUnit).toString()

        val rG = findViewById<RadioGroup>(R.id.energy_group)

        // maybe change later for prettier
        if(energyUnit == "kcal") {
            rG.check(R.id.radio_kcal)
        }
        if(energyUnit == "kJ") {
            rG.check(R.id.radio_kj)
        }

        Log.d("main", energyUnit)


    }

    //toggling changes energyUnit
    fun radioEnergy(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.radio_kcal -> {
                    if (checked) {
                        energyUnit = "kcal"
                    }
                }
                R.id.radio_kj -> {
                    if (checked) {
                        energyUnit = "kJ"
                    }
                }
            }
        }
    }

    //saving preferences
    override fun onPause() {
        super.onPause()
        Log.d("main", "pause called")
        val sharedPreference = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("energy", energyUnit)
        editor.apply()
    }
}
