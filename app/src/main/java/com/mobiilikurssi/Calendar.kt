package com.mobiilikurssi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class Calendar : AppCompatActivity() {

    private lateinit var calendarView: CalendarView

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // if from Goal go back to MapsActivity
        if (intent.getBooleanExtra("EXIT", false)) {
            finish()
        }

        setContentView(R.layout.activity_calendar)

        //TODO show dates in calendar
        calendarView = findViewById(R.id.calendarView)

        val km = intent.getFloatExtra("totalkm", 0.0f)
        val kcal = intent.getFloatExtra("totalkcal", 0.0f)
        val weightset = intent.getBooleanExtra("weightset", true)

        // textViews by default
        val goals = findViewById<TextView>(R.id.textview_goals).apply {
            this.text = "Ei uusia tavoitteita"
        }
        val myDate = findViewById<TextView>(R.id.textView_date).apply {
            this.text = ""
        }
        val completed = findViewById<TextView>(R.id.textView_completed).apply {
            this.text = ""
        }

        findViewById<Button>(R.id.button_goals).setOnClickListener {
            startActivity(Intent(this, Goal::class.java))
        }

        // getting preferences
        val pref: SharedPreferences = this.getSharedPreferences("GOAL", MODE_PRIVATE)
        val getTime = pref.getString("päivä", "empty")
        val getUnit = pref.getString("kalori", "empty")
        val getAmount = pref.getString("amount", "empty")
        val time = pref.getString("time", "empty")

        val editor = pref.edit()
        // only add if not nan and positive number
        if(!km.isNaN() && !kcal.isNaN()) {
            if(km > 0 && kcal > 0) {
                var totalkcal = pref.getFloat("totalkcal", 0.0f)
                var totalkm = pref.getFloat("totalkm", 0.0f)
                totalkm += km
                totalkcal += kcal
                editor.putFloat("totalkcal", totalkcal)
                editor.putFloat("totalkm", totalkm)
                editor.apply()
            }
        }

        // more preferences
        val prefSettings = this.getSharedPreferences("SETTINGS", MODE_PRIVATE)
        val startingweight = prefSettings.getString("weight", "empty")

        // get values from time
        var day = ""
        var month = ""
        var year = ""
        // null check
        if(time != "empty") {
            day = time?.split(".")?.get(0)?.let { removeZero(it) }.toString()
            month = time?.split(".")?.get(1)?.let { removeZero(it) }.toString()
            year = time?.split(".")?.get(2).toString()
        }

        // text explaining the goal
        var gU = ""
        when(getUnit) {
            "kalori" -> gU = "kaloria"
            "kilometri" -> gU = "kilometriä"
            "kilogramma" -> gU = "kilogrammaa"
        }

        if(getAmount != "empty" && getTime != "empty") {
            goals.text = "Tavoite: $getAmount $gU / $getTime"
        } else {
            goals.text = "Ei uusia tavoitteita"
        }

        when(getUnit) {
            "kilometri" -> {
                if (getAmount != null) {
                        completed.text = "Suoritettu %.2f".format(pref.getFloat("totalkm", 0.0f)) + " km / $getAmount km"
                }
            }
            "kilogramma" -> {
                if(startingweight != "empty") {
                    completed.text = "Tavoitepaino: ${getAmount?.toInt()?.let { startingweight?.toInt()?.minus(it) }}kg"
                } else {
                    completed.text = "Aseta painosi asetuksissa niin näet tavoitepainosi tavoiteajan kuluttua"
                }
            }
            "kalori" -> {
                if (getAmount != null) {
                    if(weightset) {
                        completed.text = "Suoritettu  %.2f".format(pref.getFloat("totalkcal", 0.0f)) + " kcal / $getAmount kcal"
                    } else {
                        completed.text = "Aseta painosi asetuksissa niin näet kaloreiden kulutuksen"
                    }

                }
            }
        }

        // settings text (starting time - ending time)
        when(getTime) {
            "päivä" -> {
                val newday = day.toInt().plus(1)
                myDate.text = "$day.$month.$year - $newday.$month.$year"
            }
            "viikko" -> {
                var daysInMonth = 0
                val y = year.toInt()
                when(month) {
                    "1", "3", "5", "7", "8", "10", "12" -> daysInMonth = 31
                    "4", "6", "9", "11" -> daysInMonth = 30
                    // leap year check
                    "2" -> daysInMonth = if(y % 4 == 0 && y % 100 != 0 || y % 400 == 0) 29 else 28
                }
                var newday = day.toInt()
                var newmonth = month.toInt()
                for(i in 0..7) {
                    when {
                        newday > daysInMonth -> newday++
                        else -> {
                            newday = 1
                            newmonth++
                        }
                    }
                }
                myDate.text = "$day.$month.$year - $newday.$newmonth.$year"
            }
            "kuukausi" -> {
                val newmonth = month.toInt().plus(1)
                myDate.text = "$day.$month.$year - $day.$newmonth.$year"
            }
            "vuosi" -> {
                val newyear = year.toInt().plus(1)
                myDate.text = "$day.$month.$year - $day.$month.$newyear"
            }
        }
    }
    // regex for replacing zero in front of number
    private fun removeZero(str : String) : String {
        val regex = "^0+(?!$)".toRegex()
        return regex.replace(str, "")
    }
}