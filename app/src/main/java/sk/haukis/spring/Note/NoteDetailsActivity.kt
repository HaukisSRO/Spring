package sk.haukis.spring.Note

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.transition.Transition
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import sk.haukis.spring.R

import kotlinx.android.synthetic.main.activity_note_details.*
import kotlinx.android.synthetic.main.content_note_details.*
import sk.haukis.spring.API.DB
import sk.haukis.spring.Models.Note
import sk.haukis.spring.commons.GalleryFragment
import sk.haukis.spring.commons.SchemeParameter
import android.view.ViewAnimationUtils
import android.animation.Animator



class NoteDetailsActivity : AppCompatActivity() {

    lateinit var templateScheme : ArrayList<SchemeParameter>
    lateinit var noteScheme : ArrayList<SchemeParameter>
    lateinit var note : Note
    val db = DB()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        gallery_fab.scaleX = 0F
        gallery_fab.scaleY = 0F

        db.Init(this)

        note = db.GetNote(intent.getStringExtra("note_id"))

        setUpLayout()
        title_image.setImageURI(Uri.parse(note.titleImage))

        toolbar.title = note.name

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        gallery_fab.post {
            val enterTransition = window.enterTransition
            enterTransition.addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(p0: Transition?) {
                    gallery_fab.animate().scaleX(1F).scaleY(1F)
                    enterTransition.removeListener(this)
                }
                override fun onTransitionResume(p0: Transition?) {}
                override fun onTransitionPause(p0: Transition?) {}
                override fun onTransitionCancel(p0: Transition?) {}
                override fun onTransitionStart(p0: Transition?) {}
            })
        }

        gallery_fab.setOnClickListener {
            val galleryFragment = GalleryFragment().newInstance(note.images)
            supportFragmentManager.beginTransaction()
                    .add(R.id.gallery_wrapper, galleryFragment)
                    .addToBackStack("gallery")
                    .commit()

            reveal(gallery_wrapper, false)
        }
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

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            reveal(gallery_wrapper, true)
        }
        else {
            gallery_fab.animate()
                    .scaleX(0F).scaleY(0F)
                    .withEndAction({
                        supportFinishAfterTransition()
                    })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                gallery_fab.animate()
                        .scaleX(0F).scaleY(0F)
                        .withEndAction({
                            supportFinishAfterTransition()
                        })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun reveal(view : View, hide : Boolean){

        val centerX = (gallery_fab.left + gallery_fab.right) / 2
        val centerY = (gallery_fab.top + gallery_fab.bottom) / 2

        if (hide){
            val endRadius = 0F
            val startRadius = Math.max(view.width, view.height).toFloat()

            val anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius)
            anim.duration = 500
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {}
                override fun onAnimationCancel(p0: Animator?) {}
                override fun onAnimationStart(p0: Animator?) {}
                override fun onAnimationEnd(p0: Animator?) {
                    gallery_fab.animate().scaleX(1F).scaleY(1F)
                    view.visibility = View.INVISIBLE
                    supportFragmentManager.popBackStackImmediate()
                }
            })
            anim.start()
        }

        else {

            gallery_fab.animate().scaleX(0F).scaleY(0F).withEndAction {
                val startRadius = 0
                val endRadius = Math.max(view.width, view.height)

                val anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius.toFloat(), endRadius.toFloat())

                view.visibility = View.VISIBLE
                anim.duration = 500
                anim.start()
            }
        }
    }

}
