package com.mobiilikurssi

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime

class Goal : AppCompatActivity() {

    private val timeArray = arrayOf<String>("päivä", "viikko", "kuukausi", "vuosi")
    private val unitArray = arrayOf<String>("kalori", "kilometri", "kilogramma")


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        val layout1 = findViewById<LinearLayout>(R.id.time_layout)
        val layout2 = findViewById<LinearLayout>(R.id.format_layout)
        val amount = findViewById<EditText>(R.id.text_height)

        val pref: SharedPreferences = this.getSharedPreferences("GOAL", MODE_PRIVATE)
        val editor = pref.edit()

        // making spinners
        createSpinner(timeArray, layout1)
        createSpinner(unitArray, layout2)

        val currentDateTime = LocalDateTime.now()
        val calendar = java.util.Calendar.getInstance()
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        calendar.set(
                currentDateTime.year, currentDateTime.monthValue, currentDateTime.dayOfMonth
        )
        val time = sdf.format(calendar.timeInMillis)

        findViewById<Button>(R.id.send_button).setOnClickListener {
            // sending preferences
            editor.putString("amount", amount.text.toString())
            editor.putString("time", time)
            //editor.putBoolean("set", set)
            editor.apply()
            startActivity(Intent(this, Calendar::class.java))
            }
        }


    private fun createSpinner(list: Array<String>, place: LinearLayout){

        // creating widget
        val spinner = Spinner(this)
        spinner.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // adding spinner to layout
        place.addView(spinner)

        val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, list
        )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
            ) {
                // setting the selected item
                spinner.setSelection(position)

                val pref: SharedPreferences = getSharedPreferences("GOAL", MODE_PRIVATE)
                val editor = pref.edit()

                // key is the first name in the class variable list
                editor.putString(list[0], spinner.selectedItem.toString())
                editor.apply()
            }
            // nothing yet
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
