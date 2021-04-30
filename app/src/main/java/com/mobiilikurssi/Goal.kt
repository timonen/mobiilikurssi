package com.mobiilikurssi

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.time.LocalDateTime

/**
 * With this class user can set up exercise goals.
 *
 * @author Valtteri Viirret
 * @version 1.0
 */
class Goal : AppCompatActivity() {

    /** Arrays for different time and unit settings */
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

        /**making spinners by calling function */
        createSpinner(timeArray, layout1)
        createSpinner(unitArray, layout2)

        /**
         * Setting up a "timestamp" for current goal.
         * Formatted as a Finnish date
         */
        val currentDateTime = LocalDateTime.now()
        val calendar = java.util.Calendar.getInstance()
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        calendar.set(
                currentDateTime.year, currentDateTime.monthValue, currentDateTime.dayOfMonth
        )
        val time = sdf.format(calendar.timeInMillis)

        findViewById<Button>(R.id.send_button).setOnClickListener {

             /** preferences for a goal*/
            editor.putString("amount", amount.text.toString())
            editor.putString("time", time)
            editor.putFloat("totalkcal", 0.0f)
            editor.putFloat("totalkm", 0.0f)
            editor.apply()

            Toast.makeText(applicationContext, "Tavoite asetettu", Toast.LENGTH_SHORT).show()

            /** intent for going back to MapsActivity */
            val intent = Intent(applicationContext, Calendar::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("EXIT", true)
            startActivity(intent)
        }
    }
    /**
     * Creating a dynamic spinner (drop-down list)
     * @param list Array of options
     * @param place Location for spinner
     */
    private fun createSpinner(list: Array<String>, place: LinearLayout){
        /** creating widget */
        val spinner = Spinner(this)
        spinner.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        /** adding to layout */
        place.addView(spinner)
        /** making an adapter */
        val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, list
        )
        spinner.adapter = adapter

        /** callback to be invoked when item has been selected*/
        spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            /**
             * Function for selecting item
             * @param parent children are list items defined in adapter
             * @param position item's position in a list
             * @param view
             * @param id
             */
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
            ) {

                val pref: SharedPreferences = getSharedPreferences("GOAL", MODE_PRIVATE)
                val editor = pref.edit()
                /**
                 * Key is the first item in the list used as a parameter
                 * Value is the selected item as a string
                 */
                editor.putString(list[0], spinner.selectedItem.toString())
                editor.apply()
            }
            /** Must be implemented because listener is abstract. */
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
