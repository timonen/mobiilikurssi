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
import java.lang.Exception

/**
 * History Class
 * @author Roope Timonen
 */
class History : AppCompatActivity() {
    lateinit var listView: ListView
    var adapter: Adapter? = null
    private val io by lazy { IOwrap(this, "") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        listView = findViewById<ListView>(R.id.historyList)

        var mainf = io.open("data.json");
        try {
            val list: MutableList<Template> = io.fromJsonToList(io.read(mainf))
            adapter = Adapter(this, list)
            listView.adapter = adapter
        } catch(e: Exception){}
    }
}

/**
 * Custom adapter for creating rows
 * @author Roope Timonen
 * @property context
 * @property arrayList
 */
class Adapter(private val context: Context, private val arrayList: MutableList<Template>) : BaseAdapter() {
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
        km.text = "Kilometrejä: ${arrayList[position].km.toString()}"
        kcal.text = "Kaloreita: ${arrayList[position].kcal.toString()}"
        date.text = arrayList[position].date
        return convertView
    }
}
