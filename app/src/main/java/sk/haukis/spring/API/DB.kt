package sk.haukis.spring.API

import android.content.Context
import ninja.sakib.pultusorm.callbacks.Callback
import ninja.sakib.pultusorm.core.PultusORM
import ninja.sakib.pultusorm.core.PultusORMCondition
import sk.haukis.spring.Model.OfflineNote
import sk.haukis.spring.Models.Note
import sk.haukis.spring.Models.Template

/**
 * Created by danie_000 on 10.7.2017.
 */
class DB {

    val dbName = "spring2.db"
    lateinit var pultusOrm : PultusORM

    fun Init (context: Context) {
        val appPath = context.filesDir.absolutePath
        pultusOrm = PultusORM(dbName, appPath)
    }

    fun Save(entity : Any){
        pultusOrm.save(entity)
    }

    fun Delete(entity : Any){
        val id : Any
        if (entity is Note)
            id = entity.id
        else if (entity is Template)
            id = entity.id

        else
            return

        val condition: PultusORMCondition = PultusORMCondition.Builder()
                .eq("id", id)
                .build()
        pultusOrm.delete(entity, condition)
    }

    fun SaveAllNotes(entities : ArrayList<Note>){
        pultusOrm.drop(Note())
        entities.forEach { note ->
            pultusOrm.save(note)
        }
    }

    fun GetAllNotes(): ArrayList<Note>{
        return pultusOrm.find(Note()) as ArrayList<Note>
    }

    fun GetAllOfflineNotes() : ArrayList<OfflineNote> {
        val offlineNotes = pultusOrm.find(OfflineNote()) as ArrayList<OfflineNote>
        offlineNotes.forEach({
            if (it.action == 0 || it.action == 1){
                val allNotes = pultusOrm.find(Note())
                if (GetNote(it.noteId) != null)
                    it.note = GetNote(it.noteId)!!
            }
        })
        return offlineNotes
    }

    fun IsOfflineNote() : Boolean {
        return pultusOrm.find(OfflineNote()).size > 0
    }

    fun GetNote(id: String) : Note? {
        val condition: PultusORMCondition = PultusORMCondition.Builder()
                .eq("id", id)
                .build()
        try {
            return pultusOrm.find(Note(), condition)[0] as Note
        }
        catch (e: Exception){
            return null
        }
    }


    fun SaveAllTemplates(entities: ArrayList<Template>){
        pultusOrm.drop(Template())
        entities.forEach { template ->
            pultusOrm.save(template)
        }
    }

    fun GetTemplate(id: Int): Template{
        val condition : PultusORMCondition = PultusORMCondition.Builder()
                .eq("id", id)
                .build()
        return pultusOrm.find(Template(), condition)[0] as Template
    }

    fun GetAllTemplates(): ArrayList<Template>{
        return pultusOrm.find(Template()) as ArrayList<Template>
    }
}