package com.mobiilikurssi

import android.content.Context
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
        return data
    }

    /**
     * Convert Template list to JSON
     * @param input
     * @return
     */
    fun toJsonFromList(input: MutableList<Template>): String {
        val data = format.encodeToString(input)
        return data
    }

    /**
     * Convert JSON to Template
     * @param json String
     * @return Template
     */
    fun fromJson(json: String): Template {
        val data = format.decodeFromString<Template>(json)
        return data
    }

    /**
     * Convert JSON array to mutable list of Templates
     * @param json
     * @return
     */
    fun fromJsonToList(json: String): MutableList<Template> {
        val data = format.decodeFromString<MutableList<Template>>(json)
        return data
    }

    /**
     * Creates Template using passed parameters
     * Adds Template to array
     * Saves to file
     * @param file
     * @param km
     * @param kcal
     * @param date
     */
    fun save(file: File, km: Float, kcal: Float, date: String){
        val t = Template(km, kcal, date)
        var tmp = open("tmp")
        try {
            var list: MutableList<Template> = fromJsonToList(read(file)) // will fail if file empty
            list.add(t)
            write(tmp,toJsonFromList(list))
        } catch (e: Exception) {
            var list: MutableList<Template> = mutableListOf()
            list.add(t)
            write(tmp,toJsonFromList(list))
        }
        overwrite(tmp,file)
    }

    /**
     * Write line to file
     * @param file
     * @param line
     */
    fun writeLine(file : File, line : String){
        file.appendText(line+"\n")
    }

    /**
     * Write to file
     * @param file
     * @param text
     */
    fun write(file : File, text : String){
        file.appendText(text)
    }

    /**
     * Overwrites file
     * @param tmp new file
     * @param perm staying file
     */
    fun overwrite(tmp : File, perm :File){
        tmp.copyTo(perm, true)
        tmp.delete()
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
        if(file.exists() || fileName == "tmp")
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
