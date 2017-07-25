package sk.haukis.spring.Note


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_note_create.*
import sk.haukis.spring.API.DB
import sk.haukis.spring.Models.Note

import sk.haukis.spring.R
import sk.haukis.spring.commons.SchemeParameter


/**
 * A simple [Fragment] subclass.
 */
class NoteCreateFragment : Fragment() {

    val db = DB()
    lateinit var scheme : ArrayList<SchemeParameter>
    var schemeId : Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_note_create, container, false)
    }

    fun Init(templateId: Int){
        db.Init(context)
        schemeId = templateId
        val template = db.GetTemplate(templateId)
        scheme = SchemeParameter().parse(template.scheme!!)
        setUpParams()
    }

    fun setUpParams(){
        for (param in scheme){
            val et = EditText(context)
            et.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            et.hint = param.text
            et.id = param.id
            param_wrapper.addView(et)
        }
    }

    fun getNote() : Note {
        val note = Note()
        note.templateId = schemeId
        val noteScheme = ArrayList<SchemeParameter>()

        note.name = note_name.text.toString()

        scheme
                .map { view?.findViewById(it.id) as EditText }
                .mapTo(noteScheme) { SchemeParameter(it.id, it.text.toString()) }

        note.text = Gson().toJson(noteScheme)

        return note
    }

}// Required empty public constructor
