package com.mobiilikurssi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

/**
 * History Class
 * @author Roope Timonen
 */
class History : AppCompatActivity() {
    lateinit var listView: ListView
    var adapter: MyAdapter? = null
    private val io by lazy { IOwrap(this, "") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        listView = findViewById<ListView>(R.id.historyList)

        var mainf = io.open("data.json");
        val list: MutableList<Template> = io.fromJsonToList(io.read(mainf))

        adapter = MyAdapter(this, list)
        listView.adapter = adapter
    }
}

/**
 * Custom adapter for creating rows
 * @property context
 * @property arrayList
 */
class MyAdapter(private val context: Context, private val arrayList: MutableList<Template>) : BaseAdapter() {
    private lateinit var km: TextView
    private lateinit var kcal: TextView
    private lateinit var date: TextView

    /**
     * @return arrayList size
     */
    override fun getCount(): Int = arrayList.size

    /**
     * @param position
     * @return position
     */
    override fun getItem(position: Int): Any = position

    /**
     * @param position
     * @return
     */
    override fun getItemId(position: Int): Long = position.toLong()

    /**
     * Fills row
     * @param position
     * @param convertView
     * @param parent
     * @return finished row
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.row, parent, false)
        km = convertView.findViewById(R.id.row_template_km)
        kcal = convertView.findViewById(R.id.row_template_kcal)
        date = convertView.findViewById(R.id.row_template_date)
        km.text = arrayList[position].km.toString()
        kcal.text = arrayList[position].kcal.toString()
        date.text = arrayList[position].date
        return convertView
    }
}
