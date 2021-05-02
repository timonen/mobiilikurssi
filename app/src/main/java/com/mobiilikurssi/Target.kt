package com.mobiilikurssi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import kotlin.math.pow

/**
 * This class shows off the current Goal and its progress
 * @author Valtteri Viirret
 * @version 1.3
 */
class Target : AppCompatActivity() {

    /**
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target)

        // if from Goal go back to MapsActivity
        if (intent.getBooleanExtra("EXIT", false)) {
            finish()
        }

        // get intent
        val km = intent.getFloatExtra("totalkm", 0.0f)
        val kcal = intent.getFloatExtra("totalkcal", 0.0f)
        val weightset = intent.getBooleanExtra("weightset", true)
        val tracking = intent.getBooleanExtra("addProgression", false)

        // define textView
        val goals = findViewById<TextView>(R.id.textview_goals)
        val myDate = findViewById<TextView>(R.id.textView_date)
        val completed = findViewById<TextView>(R.id.textView_completed)
        val percentage = findViewById<TextView>(R.id.textView_percentage)

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

        // user preferences
        val prefSettings = this.getSharedPreferences("SETTINGS", MODE_PRIVATE)
        val weight = prefSettings.getString("weight", "empty")
        val height = prefSettings.getString("height", "empty")

        // input sanitization for weight goal
        if(getUnit == "kilogramma") {
            if (getAmount != null) {
                val w = getAmount.toInt()
                when (getTime) {
                    "päivä" -> if (w > 1)  getAmount = "unreal"
                    "viikko" -> if (w > 4)  getAmount = "unreal"
                    "kuukausi" -> if (w > 10)  getAmount = "unreal"
                }
                // if goalweight is below bmi 15 (anorexia)
                val goalWeight = weight?.toInt()?.minus(w)
                val bmi = height!!.toDouble().div(100).let { goalWeight?.div(it.pow(2)) }
                if(bmi!! < 15.0) {
                    getAmount = "unreal"
                }
            }
        }

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

        // Get values from time, these time values describe time when goal was set
        var day = ""
        var month = ""
        var year = ""
        if(time != "empty") {
            day = time?.split(".")?.get(0)?.let { RZ(it) }.toString()
            month = time?.split(".")?.get(1)?.let { RZ(it) }.toString()
            year = time?.split(".")?.get(2).toString()
        }

        // text explaining the goal
        var gU = ""
        when(getUnit) {
            "kalori" -> gU = "kaloria"
            "kilometri" -> gU = "kilometriä"
            "kilogramma" -> gU = "kilogrammaa"
        }
        if(getAmount != "unreal" && getTime != "empty") {
            goals.text = "Tavoite: $getAmount $gU / $getTime"
        } else
            goals.text = "Ei uusia tavoitteita"

        /** Logic for different units */
        when(getUnit) {
            "kilometri" -> {
                if (getAmount != null) {
                    val amount = pref.getFloat("totalkm", 0.0f)
                    if (amount < getAmount.toInt()!!) {
                        completed.text = "Suoritettu %.2f".format(amount) + " km / $getAmount km"
                        calculatePercentage(percentage, amount.toDouble(), getAmount.toDouble())
                    } else
                        completed.text = "Tavoite suoritettu!"
                }
            }
            "kilogramma" -> {
                if (weight != "empty") {
                    if (getAmount != "unreal") {
                        completed.text = "Tavoitepaino: ${getAmount?.toInt()?.let { weight?.toInt()?.minus(it) }}kg"
                    } else completed.text = "Tavoitepainosi ei ole realistinen, aseta realistinen tavoite"
                } else completed.text = "Aseta painosi asetuksissa niin näet tavoitepainosi tavoiteajan kuluttua"
            }
            "kalori" -> {
                if (getAmount != null) {
                    if (weightset) {
                        val amount = pref.getFloat("totalkcal", 0.0f)
                        if (amount < getAmount.toInt()) {
                            completed.text = "Suoritettu  %.2f".format(amount) + " kcal / $getAmount kcal"
                            calculatePercentage(percentage, amount.toDouble(), getAmount.toDouble())
                        } else
                            completed.text = "Tavoite suoritettu!"
                    } else
                        completed.text = "Aseta painosi asetuksissa niin näet kaloreiden kulutuksen"
                }
            }
        }

        // getting current date
        val currentDateTime = LocalDateTime.now()
        val calendar = java.util.Calendar.getInstance()
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        calendar.set(
                currentDateTime.year, currentDateTime.monthValue, currentDateTime.dayOfMonth
        )
        val currentTime = sdf.format(calendar.timeInMillis)
        val currentDay = currentTime?.split(".")?.get(0)?.let { RZ(it) }
        val currentMonth = currentTime?.split(".")?.get(1)?.let { RZ(it) }
        val currentYear = currentTime?.split(".")?.get(2)?.let { RZ(it) }

        /** Time for goal (starting time - ending time) */
        val myG = "Tavoiteaika: "
        when(getTime) {
            "päivä" -> {
                val newday = day.toInt().plus(1)

                if(currentDay!! > day) {
                    myDate.text = "Tavoiteaika on päättynyt"
                } else
                    myDate.text = "$myG${RZ(day)}.${RZ(month)}.$year - $newday.${RZ(month)}.$year"
            }
            "viikko" -> {
                var daysInMonth = 0
                val y = year.toInt()
                when (month) {
                    "1", "3", "5", "7", "8", "10", "12" -> daysInMonth = 31
                    "4", "6", "9", "11" -> daysInMonth = 30
                    // leap year check
                    "2" -> daysInMonth = if (y % 4 == 0 && y % 100 != 0 || y % 400 == 0) 29 else 28
                }
                var newday = day.toInt()
                var newmonth = month.toInt()
                for (i in 1..7) {
                    if (newday < daysInMonth) {
                        newday += 1
                    } else {
                        newmonth += 1
                        newday = 1
                    }
                }

                if(currentDay!! > day && currentMonth!! > month) {
                    myDate.text = "Tavoiteaika on päättynyt"
                } else
                    myDate.text = "$myG${RZ(day)}.${RZ(month)}.$year - $newday.$newmonth.$year"
            }
            "kuukausi" -> {
                val newmonth = month.toInt().plus(1)

                if(currentDay!! > day && currentMonth!! > month && currentYear!! > year) {
                    myDate.text = "Tavoiteaika on päättynyt"
                } else
                    myDate.text = "$myG${RZ(day)}.${RZ(month)}.$year - $day.$newmonth.$year"
            }
            "vuosi" -> {
                val newyear = year.toInt().plus(1)

                if(currentDay!! > day && currentMonth!! > month && currentYear!! > year) {
                    myDate.text = "Tavoiteaika on päättynyt"
                } else
                    myDate.text = "$myG${RZ(day)}.${RZ(month)}.$year - $day.$month.$newyear"
            }
        }
    }

    /**
     * Regex for replacing zero in front of number
     * @param str input string
     * @return replaced string
     */
    private fun RZ(str: String) : String {
        val regex = "^0+(?!$)".toRegex()
        return regex.replace(str, "")
    }

    /**
     * Set TextView as percentage of x, y
     * @param t TextView element
     * @param x
     * @param y
     */

    private fun calculatePercentage(t: TextView, x: Double, y: Double) {
        t.text = " %.1f".format((x / y) * 100) + "%"
    }
}

