package com.mobiilikurssi

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.PrintWriter
import kotlin.math.log
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Template(val km: Double, val kcal: Int, val date: String)

/**
 * @author Roope Timonen
 * @constructor Set directory to operate in
 * @param ctx Context of app
 * @param path Path of directory
 */
class IOwrap(ctx : Context, path : String) {
    private val basePath : File = ctx.filesDir
    private val dir = File(basePath, path)

    /**
     * Test json to object conversion
     */
    fun fromJson() {
        val format = Json { isLenient = true }
        val data = format.decodeFromString<Template>("""
        { 
            km   : 100,
            kcal  : 5000
            date : "2.5.2021"
        }
    """)

        Log.d("IOwrap", "${data.km}")

    }

    /**
     * Test object to json conversion
     */
    fun toJson(){
        val format = Json { isLenient = true }
        val data = Template(10.0, 102, "10.2.2021")
        Log.d("IOwrap", "$data")
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
    fun create(fileName : String): File {
        dir.mkdirs()
        val file = File(dir, fileName)
        if(file.exists())
            return file
        else
            file.appendText("")
            return file;
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
