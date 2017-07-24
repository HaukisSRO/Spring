package sk.haukis.spring.Models

import android.os.Environment
import ninja.sakib.pultusorm.annotations.Ignore
import ninja.sakib.pultusorm.annotations.PrimaryKey
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
    var created: String? = null
    var edited: String? = null
    var editedBy: String = ""
    var templateId: Int = 0

    @Ignore
    var images : java.util.ArrayList<String> = java.util.ArrayList()
        get() {
            val mediaStorageDir = File(Environment.getExternalStorageDirectory().toString()
                    + "/Android/data/"
                    + "sk.haukis.spring"
                    + "/Files/"
                    + id).listFiles()
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
                    + id).listFiles()
            if (mediaStorageDir != null && mediaStorageDir.isNotEmpty()) {
                mediaStorageDir.mapTo(images) { it.absolutePath }
                return mediaStorageDir[0].absolutePath
            }
            return ""
        }
}