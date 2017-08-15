package sk.haukis.spring.Model

import ninja.sakib.pultusorm.annotations.Ignore
import sk.haukis.spring.Models.Note

class OfflineNote {

    var id : String = ""
    var action: Int = 0 // 0 = Create, 1 = Edit, 2 = Delete

    @Ignore
    var note: Note = Note()
    var noteId: String = ""
}