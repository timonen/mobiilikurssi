package com.mobiilikurssi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*

class Goal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_goal)

        val layout1 = findViewById<LinearLayout>(R.id.time_layout)
        val layout2 = findViewById<LinearLayout>(R.id.format_layout)


        createSpinner(R.array.time, layout1)
        createSpinner(R.array.format, layout2)


    }

    private fun createSpinner(list: Int, place: LinearLayout) {

        val times = resources.getStringArray(list)

        // android widget not class
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
                        // code here what to do with the selected item
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}

                }
    }
}



