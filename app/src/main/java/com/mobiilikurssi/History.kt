package com.mobiilikurssi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import java.io.File

class History : AppCompatActivity() {
    lateinit var listView: ListView
    var arrayList: ArrayList<Template> = ArrayList()
    var adapter: MyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        listView = findViewById<ListView>(R.id.historyList)

        arrayList.add(Template(10.0, 100, "1.1.2021"))
        arrayList.add(Template(20.0, 200, "2.2.2020"))
        arrayList.add(Template(30.0, 300, "1.1.2020"))

        adapter = MyAdapter(this, arrayList)
        listView.adapter = adapter

    }
}

class MyAdapter(private val context: Context, private val arrayList: java.util.ArrayList<Template>) : BaseAdapter() {
    private lateinit var km: TextView
    private lateinit var kcal: TextView
    private lateinit var date: TextView
    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.row, parent, false)
        km = convertView.findViewById(R.id.row_template_km)
        kcal = convertView.findViewById(R.id.row_template_kcal)
        date = convertView.findViewById(R.id.row_template_date)
        km.text = " " + arrayList[position].km
        kcal.text = arrayList[position].kcal.toString()
        date.text = arrayList[position].date
        return convertView
    }
}
