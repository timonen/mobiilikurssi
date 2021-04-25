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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*



class Calendar : AppCompatActivity() {

    private lateinit var calendarView: CalendarView

    //abstract class Calendar : Serializable, Cloneable, Comparable<Calendar!>


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        calendarView = findViewById(R.id.calendarView)
        val tb = findViewById<Button>(R.id.test_button)

        // lot of bugs when trying to import this
        val calendar = java.util.Calendar.getInstance()




        //val ft = sdf.format(time)
        // L//og.d("main", ft)



        tb.setOnClickListener {
           // calendar.set(
               //currentDateTime.year, currentDateTime.monthValue, currentDateTime.dayOfMonth
            //)

            //myDate.text = sdf.format(calendar.timeInMillis)
        }


        // ANDROID API API PLATFORM 30 FEATURES
        //val temp = calendarView.getTime()
        //ate = calendarView.getInstance()



        //val sdf = SimpleDateFormat("yy-MM-dd")
        //val currentdate = sdf.format
        //myDate.text = temp.toString()
                /*
        calendarView.setOnDateChangeListener(CalendarView.OnDateChangeListener {
            _, year, month, dayOfMonth −>
            val date = dayOfMonth.toString() + "−" + (month + 1) + "−" + year
            myDate.text = date
        })


            //val current = LocalDateTime.now()
                 */


        val sdf = SimpleDateFormat("dd.MM.yyyy")

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
        val year = pref.getString("year", "empty")
        val month = pref.getString("month", "empty")
        val day = pref.getString("day", "empty")
        val time = pref.getString("time", "empty")

        if (time != null) {
            Log.d("main", sdf.format(time))
        }
        //val set = pref.getBoolean("set", false)

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
                //val newweek = day.toInt().plus(7)
                //myDate.text = "$day.$month.$year - $newday.$month.$year"
            }
        }

    }


}