package com.mobiilikurssi

import android.content.Context
import android.util.Log
import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
 * Template for saving past trips
 * @property km Float
 * @property kcal Float
 * @property date Date as String
 */
@Serializable
data class Template(val km: Float, val kcal: Float, val date: String)

/**
 * @author Roope Timonen
 * @constructor Set directory to operate in
 * @param ctx Context of app
 * @param path Path of directory
 */
class IOwrap(ctx : Context, path : String) {
    private val basePath : File = ctx.filesDir
    private val dir = File(basePath, path)
    val format = Json { isLenient = true }

    /**
     * Convert Template to JSON
     * @param input Template
     * @return JSON String
     */
    fun toJson(input: Template): String {
        val data = format.encodeToString(input)
        Log.d("IOwrap", "$data")
        return data
    }

    /**
     * Convert JSON to Template
     * @param json String
     * @return Template
     */
    fun fromJson(json: String): Template {
        val data = format.decodeFromString<Template>(json)
        Log.d("IOwrap", "${data.km}")
        return data
    }

    /**
     * Convert JSON array to mutable list of Templates
     * @param json
     * @return
     */
    fun fromJsonToList(json: String): MutableList<Template> {
        val data = format.decodeFromString<MutableList<Template>>(json)
        Log.d("IOwrap", "$data")
        return data
    }

    /**
     * TODO finish this
     *
     * Saves passed values as JSON object in file
     * @param km
     * @param kcal
     * @param date
     */
    fun save(f: File, km: Float, kcal: Float, date: String){
        val tmp = toJson(Template(km,kcal,date));
        writeLine(f,tmp)
    }

    /**
     * Write to line to file
     * @param file
     * @param record
     */
    fun writeLine(file : File, line : String){
        file.appendText(line+"\n")
    }

    /**
     * Creates directory
     * Check if file exists if not, creates it
     * @param fileName Filename
     * @return Created file
     */
    fun open(fileName : String): File {
        dir.mkdirs()
        val file = File(dir, fileName)
        if(file.exists())
            return file
        else
            file.appendText("")
            return file
    }

    /**
     * Reads passed file
     * @param file
     * @return file contents as string
     */
    fun read(file : File): String {
        return file.readText()
    }

}
