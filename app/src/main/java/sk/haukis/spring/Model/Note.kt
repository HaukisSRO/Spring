package sk.haukis.spring.Models

import android.os.Environment
import android.util.Log
import co.metalab.asyncawait.async
import ninja.sakib.pultusorm.annotations.Ignore
import ninja.sakib.pultusorm.annotations.PrimaryKey
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.haukis.spring.API.SpringApi
import sk.haukis.spring.Model.NoteImage
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by danie_000 on 4.7.2017.
 */

class Note {
    @PrimaryKey
    var id : String = ""
    var name: String = ""
    var owner: String = ""
    var text: String = ""
    var created: String = "1.1.1995"
    var edited: String = "1.1.1995"
    var editedBy: String = ""
    var templateId: Int = 0
    var color : String = ""
    var isPublic : Boolean = false

    @Ignore
    var onlineImages : ArrayList<NoteImage> = ArrayList()

    @Ignore
    var images : java.util.ArrayList<String> = java.util.ArrayList()
        get() {
            val mediaStorageDir = File(Environment.getExternalStorageDirectory().toString()
                    + "/Android/data/"
                    + "sk.haukis.spring"
                    + "/Files/"
                    + id +"/Images").listFiles()
            val a : java.util.ArrayList<String> = java.util.ArrayList()
            if (mediaStorageDir != null && mediaStorageDir.isNotEmpty()) {
                mediaStorageDir.mapTo(a) { it.absolutePath }
                return a
            }
            return java.util.ArrayList()
        }

    val titleImage : String
        get() {
            val mediaStorageDir = File(Environment.getExternalStorageDirectory().toString()
                    + "/Android/data/"
                    + "sk.haukis.spring"
                    + "/Files/"
                    + id +"/Images").listFiles()
            if (mediaStorageDir != null && mediaStorageDir.isNotEmpty()) {
                mediaStorageDir.mapTo(images) { it.absolutePath }
                return mediaStorageDir[0].absolutePath
            }
            return ""
        }


    fun Create(springApi: SpringApi){
        val noteSaveCall = springApi.createNote(this)
        noteSaveCall.enqueue(object : Callback<Note> {
            override fun onFailure(call: Call<Note>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<Note>?, response: Response<Note>?) {
                Log.e("NoteCreate", "Saved $name")
            }
        })
    }
}