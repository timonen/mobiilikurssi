package com.mobiilikurssi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

/**
 * TODO write docs
 * Calendar class
 * @author
 * @version 1.3
 */
class Calendar : AppCompatActivity() {
    private lateinit var calendarView: CalendarView

    /**
     * TODO
     *
     * @param savedInstanceState
     */
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

        // get intent
        val km = intent.getFloatExtra("totalkm", 0.0f)
        val kcal = intent.getFloatExtra("totalkcal", 0.0f)
        val weightset = intent.getBooleanExtra("weightset", true)
        val tracking = intent.getBooleanExtra("addProgression", false)

        val goals = findViewById<TextView>(R.id.textview_goals)
        val myDate = findViewById<TextView>(R.id.textView_date)
        val completed = findViewById<TextView>(R.id.textView_completed)

        findViewById<Button>(R.id.button_goals).setOnClickListener {
            startActivity(Intent(this, Goal::class.java))
        }

        // getting goal preferences
        val pref: SharedPreferences = this.getSharedPreferences("GOAL", MODE_PRIVATE)
        val getTime = pref.getString("päivä", "empty")
        val getUnit = pref.getString("kalori", "empty")
        var getAmount = pref.getString("amount", "empty")
        val time = pref.getString("time", "empty")
        val editor = pref.edit()


        // input sanitation
        if(getUnit == "kilogramma") {
            if (getAmount != null) {
                when (getTime) {
                    "päivä" -> {
                        if (getAmount.toInt() > 1) {
                            getAmount = "unreal"
                        }
                    }

                    "viikko" -> {
                        if (getAmount.toInt() > 4) {
                            getAmount = "unreal"
                        }
                    }
                    "kuukausi" -> {
                        if (getAmount.toInt() > 10) {
                            getAmount = "unreal"
                        }
                    }
                }
            }
        }

        // user preferences
        val prefSettings = this.getSharedPreferences("SETTINGS", MODE_PRIVATE)
        val startingweight = prefSettings.getString("weight", "empty")


        /** Updating Goal preferences from MapsActivity intent  */
        if((!km.isNaN() && !kcal.isNaN()) && (km > 0 && kcal > 0)) {
            if(tracking) {
                var totalkcal = pref.getFloat("totalkcal", 0.0f)
                var totalkm = pref.getFloat("totalkm", 0.0f)
                totalkm += km
                totalkcal += kcal
                editor.putFloat("totalkcal", totalkcal)
                editor.putFloat("totalkm", totalkm)
                editor.apply()
            }
        }

        /** Get values from time */
        var day = ""
        var month = ""
        var year = ""
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
        } else
            goals.text = "Ei uusia tavoitteita"


        /** Logic for different units */
        when(getUnit) {
            "kilometri" -> {
                if (getAmount != null) {
                    val amount = pref.getFloat("totalkm", 0.0f)
                    if(amount < getAmount.toInt()) {
                        completed.text = "Suoritettu %.2f".format(amount) + " km / $getAmount km"
                    } else
                        completed.text = "Tavoite suoritettu!"
                }
            }
            "kilogramma" -> {
                if(startingweight != "empty") {
                    if(getAmount != "unreal") {
                        completed.text = "Tavoitepaino: ${getAmount?.toInt()?.let { startingweight?.toInt()?.minus(it) }}kg"
                    }
                    else completed.text = "Tavoitepainosi ei ole realistinen, aseta realistinen tavoite"
                }
                else completed.text = "Aseta painosi asetuksissa niin näet tavoitepainosi tavoiteajan kuluttua"
            }
            "kalori" -> {
                if (getAmount != null) {
                    if(weightset) {
                        val amount = pref.getFloat("totalkcal", 0.0f)
                        if(amount < getAmount.toInt()) {
                            completed.text = "Suoritettu  %.2f".format(amount) + " kcal / $getAmount kcal"
                        } else
                            completed.text = "Tavoite suoritettu!"
                    } else
                        completed.text = "Aseta painosi asetuksissa niin näet kaloreiden kulutuksen"
                }
            }
        }

        /** Time for goal (starting time - ending time) */
        val myG = "Tavoiteaika: "
        when(getTime) {
            "päivä" -> {
                val newday = day.toInt().plus(1)
                myDate.text = "$myG$day.$month.$year - $newday.$month.$year"
            }
            "viikko" -> {
                var daysInMonth = 0
                val y = year.toInt()
                when(month) {
                    "1", "3", "5", "7", "8", "10", "12" -> daysInMonth = 31
                    "4", "6", "9", "11" -> daysInMonth = 30
                    /** leap year check */
                    "2" -> daysInMonth = if(y % 4 == 0 && y % 100 != 0 || y % 400 == 0) 29 else 28
                }
                var newday = day.toInt()
                var newmonth = month.toInt()
                for(i in 1..7) {
                    if(newday < daysInMonth){
                        newday += 1
                    } else
                        newday = 1
                        newmonth += 1

                }
                myDate.text = "$myG$day.$month.$year - $newday.$newmonth.$year"
            }
            "kuukausi" -> {
                val newmonth = month.toInt().plus(1)
                myDate.text = "$myG$day.$month.$year - $day.$newmonth.$year"
            }
            "vuosi" -> {
                val newyear = year.toInt().plus(1)
                myDate.text = "$myG$day.$month.$year - $day.$month.$newyear"
            }
        }
    }

    /**
     * Regex for replacing zero in front of number
     * @param str input string
     * @return replaced string
     */
    private fun removeZero(str : String) : String {
        val regex = "^0+(?!$)".toRegex()
        return regex.replace(str, "")
    }

    /**
     * Will be used with calendar
     * TODO make it work
     * @param view
     */

    fun calendarClick(view: View) {
        calendarView = findViewById(R.id.calendarView)
        val selectedDate: Long = calendarView.date
        Log.i("test", "$selectedDate");
    }



}