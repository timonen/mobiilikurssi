package com.mobiilikurssi

import android.R
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class Spinner(private val array: Int, private val layout: Int, private val place: LinearLayout) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layout)

        val times = resources.getStringArray(array)

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
                R.layout.simple_spinner_item, times
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

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }
}
