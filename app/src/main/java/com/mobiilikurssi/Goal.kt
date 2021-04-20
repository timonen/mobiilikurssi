package com.mobiilikurssi

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*

class Goal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        val layout1 = findViewById<LinearLayout>(R.id.time_layout)
        val layout2 = findViewById<LinearLayout>(R.id.format_layout)
        val amount = findViewById<EditText>(R.id.text_amount)

        val pref: SharedPreferences = this.getSharedPreferences("GOAL", MODE_PRIVATE)
        val editor = pref.edit()

        // making spinners
        createSpinner(R.array.time, layout1)
        createSpinner(R.array.format, layout2)

        findViewById<Button>(R.id.send_button).setOnClickListener {
            // sending preferences from EditText
            // string now changinging to Int prolly soon
            editor.putString("amount", amount.text.toString())
            editor.apply()
            }
        }


    private fun createSpinner(list: Int, place: LinearLayout){
        val times = resources.getStringArray(list)

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
                android.R.layout.simple_spinner_item, times
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

                // getting preferences = listname.toString()
                editor.putString(list.toString(), spinner.selectedItem.toString())
                editor.apply()
            }
            // nothing yet
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
