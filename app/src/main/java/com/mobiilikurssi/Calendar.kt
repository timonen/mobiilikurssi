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

    //abstract class Calendar : Serializable, Cloneable, Comparable<Calendar!>


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        calendarView = findViewById(R.id.calendarView)

        val goals = findViewById<TextView>(R.id.textview_goals)
        val completed = findViewById<TextView>(R.id.textView_completed)
        val myDate = findViewById<TextView>(R.id.textView_date)


        findViewById<Button>(R.id.button_goals).setOnClickListener {
            startActivity(Intent(this, Goal::class.java))
        }

        val pref: SharedPreferences = this.getSharedPreferences("GOAL", MODE_PRIVATE)
        val getTime = pref.getString("päivä", "empty")
        val getUnit = pref.getString("kalori", "empty")
        val getAmount = pref.getString("amount", "empty")

        val time = pref.getString("time", "empty")
        val day = time?.split(".")?.get(0)?.let { removeZero(it) }
        val month = time?.split(".")?.get(1)?.let { removeZero(it) }
        val year = time?.split(".")?.get(2)

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

        when(getTime) {
            "päivä" -> {
                val newday = day?.toInt()?.plus(1)
                myDate.text = "$day.$month.$year - $newday.$month.$year"
            }
            "viikko" -> {
                var daysInMonth = 0
                when(month) {
                    "1", "3", "5", "7", "8", "10", "12" -> daysInMonth = 31
                    "4", "6", "9", "11" -> daysInMonth = 30
                    // this will work until 2027
                    "2" -> {
                        if(year == "2024"){
                            daysInMonth = 29
                        } else {
                            daysInMonth = 28
                        }
                    }
                }
                Log.d("main", daysInMonth.toString())
                var newday = day?.toInt()
                var newmonth = month?.toInt()
                for(i in 0..7) {
                        if (newday != null) {
                            if (newmonth != null) {
                                if(newday < daysInMonth){
                                    newday += 1
                                } else {
                                    newday = 1
                                    newmonth += 1
                                }
                            }
                        }
                }
                myDate.text = "$day.$month.$year - $newday.$newmonth.$year"
            }
            "kuukausi" -> {
                val newmonth = month?.toInt()?.plus(1)
                myDate.text = "$day.$month.$year - $day.$newmonth.$year"
            }
            "vuosi" -> {
                val newyear = year?.toInt()?.plus(1)
                myDate.text = "$day.$month.$year - $day.$month.$newyear"
            }
        }
    }

    private fun removeZero(str : String) : String {
        val regex = "^0+(?!$)".toRegex()
        return regex.replace(str, "")
    }

}