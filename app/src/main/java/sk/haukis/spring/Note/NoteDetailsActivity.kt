package sk.haukis.spring.Note

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import sk.haukis.spring.R

import kotlinx.android.synthetic.main.activity_note_details.*
import kotlinx.android.synthetic.main.content_note_details.*
import sk.haukis.spring.API.DB
import sk.haukis.spring.Models.Note
import sk.haukis.spring.commons.SchemeParameter

class NoteDetailsActivity : AppCompatActivity() {

    lateinit var templateScheme : ArrayList<SchemeParameter>
    lateinit var noteScheme : ArrayList<SchemeParameter>
    lateinit var note : Note
    val db = DB()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        setSupportActionBar(toolbar)

        db.Init(this)

        note = db.GetNote(intent.getStringExtra("note_id"))

        setUpLayout()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_image.setImageURI(Uri.parse(note.titleImage))

        toolbar.title = note.name
    }

    fun setUpLayout(){
        val template = db.GetTemplate(note.templateId)
        templateScheme = SchemeParameter().parse(template.scheme!!)
        noteScheme = SchemeParameter().parse(note.text)

        for (i in 0..templateScheme.size - 1){
            addTemplateParam(templateScheme[i])
            addNoteParam(noteScheme[i])
        }
    }

    fun addNoteParam(param: SchemeParameter){
        val tv = TextView(this)
        tv.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tv.text = param.text
        tv.textScaleX = 1.1F
        param_wrapper.addView(tv)
    }

    fun addTemplateParam(param: SchemeParameter){
        val tv = TextView(this)
        tv.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tv.text = param.text
        tv.textScaleX = 0.8F
        param_wrapper.addView(tv)
    }

}
