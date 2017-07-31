package sk.haukis.spring.Model

import ninja.sakib.pultusorm.annotations.Ignore
import java.io.File

/**
 * Created by Daniel on 31. 7. 2017.
 */
class NoteImage {
    var id : String = ""
    var noteId : String = ""
    var desc : String = ""
    var ownerId : String = ""
    var path : String = ""

    @Ignore
    var image : File? = null
}