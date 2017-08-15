package sk.haukis.spring.Note

import android.graphics.Color
import android.net.Uri
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v4.app.FragmentStatePagerAdapter
import android.transition.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import co.metalab.asyncawait.async
import kotlinx.android.synthetic.main.activity_note_create.*
import kotlinx.android.synthetic.main.fragment_notes.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.haukis.spring.API.DB
import sk.haukis.spring.API.SpringApi
import sk.haukis.spring.Model.NoteImage
import sk.haukis.spring.Model.OfflineNote
import sk.haukis.spring.Models.Note
import sk.haukis.spring.R
import sk.haukis.spring.commons.CameraFragment
import sk.haukis.spring.commons.removeFragment
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class NoteCreateActivity : AppCompatActivity(), NoteSchemeChooseFragment.OnTemplateChooseListener, NoteMediaFragment.OpenCameraListener, CameraFragment.MediaCreateListener {

    lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    val templateChooser : NoteSchemeChooseFragment = NoteSchemeChooseFragment()
    val noteMedia : NoteMediaFragment = NoteMediaFragment()
    val noteCreate : NoteCreateFragment = NoteCreateFragment()
    val noteSpecs : NoteSpecsFragment = NoteSpecsFragment()
    val camera : CameraFragment = CameraFragment()
    var NoteId = UUID.randomUUID().toString()
    val images : ArrayList<NoteImage> = ArrayList()
    var editMode = false
    lateinit var springApi : SpringApi
    var db : DB = DB()

    override fun onPhotoCreated(uri: Uri) {
        val noteImage = NoteImage()

        noteImage.image = File(uri.toString())
        noteImage.noteId = NoteId
        noteImage.desc = "obrázok $uri"
        images.add(noteImage)
        noteMedia.addImage(uri)

        tabs.visibility = View.VISIBLE
        container.visibility = View.VISIBLE
        toolbar.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
                .remove(camera)
                .commit()
    }

    override fun onTemplateChoose(id: Int) {
        tabs.visibility = View.VISIBLE
        container.visibility = View.VISIBLE
        toolbar.visibility = View.VISIBLE
        save_note.visibility = View.VISIBLE
        noteCreate.Init(id)
        this.removeFragment(templateChooser)
    }

    override fun onOpenCamera(method: Int) {
        tabs.visibility = View.GONE
        toolbar.visibility = View.GONE
        val bundle = Bundle()
        bundle.putString("note_id", NoteId)
        camera.arguments = bundle

        supportFragmentManager.beginTransaction()
                .add(R.id.main_container, camera)
                .commit()
    }

    override fun onMediaPicker() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_create)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        db.Init(this)

        editMode = intent.getBooleanExtra("editMode", false)


        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        val bundle = Bundle()
        bundle.putString("noteId", NoteId)
        noteMedia.arguments = bundle
        mSectionsPagerAdapter.addPage(noteCreate, "Template")
        mSectionsPagerAdapter.addPage(noteMedia, "Media")
        mSectionsPagerAdapter.addPage(noteSpecs, "Specs")

        // Set up the ViewPager with the sections adapter.
        container.offscreenPageLimit = 3
        container!!.adapter = mSectionsPagerAdapter

        tabs.setupWithViewPager(container, true)

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        springApi = SpringApi(this)

        save_note.setOnClickListener {
            saveNote()
        }

        if (!editMode){
            supportFragmentManager.beginTransaction()
                    .add(R.id.main_container, templateChooser)
                    .commit()

            tabs.visibility = View.GONE
            container.visibility = View.INVISIBLE
            save_note.visibility = View.GONE
        }

        container.post {
            if (editMode) {
                NoteId = intent.getStringExtra("noteId")
                val note = db.GetNote(NoteId)
                noteCreate.Init(note?.templateId ?: 0, note)

                noteSpecs.Init(note!!)
            }
        }
    }

    fun saveNote(){
        val note = noteCreate.getNote()
        note.id = NoteId
        note.isPublic = noteSpecs.isPublic()
        note.color = noteSpecs.pickedColor
        if (springApi.isOnline()) {
            async {
                var noteSaveCall = springApi.createNote(note)
                if (editMode)
                    noteSaveCall = springApi.editNote(NoteId, note)

                noteSaveCall.enqueue(object : Callback<Note> {
                    override fun onFailure(call: Call<Note>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<Note>?, response: Response<Note>?) {
                        Log.e("NoteCreate", "Saved ${note.name}")
                    }
                })
            }
        } else {
            db.Save(note)

            val offlineNote = OfflineNote()
            offlineNote.id = UUID.randomUUID().toString()
            offlineNote.noteId = note.id
            offlineNote.action = 0
            if (editMode)
                offlineNote.action = 1

            db.Save(offlineNote)
        }
        async {
            images
                    .map { springApi.uploadImages(it) }
                    .forEach {
                        it.enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                                Log.e("Photos", "saved")
                            }

                            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                            }

                        })
                    }
        }
        finishAfterTransition()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_note_create, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                return true
            }
            android.R.id.home -> {
                finishActivity()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun finishActivity(){
        finishAfterTransition()
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        val fragmentList : ArrayList<Fragment> = ArrayList()
        val titleList : ArrayList<String> = ArrayList()

        fun addPage(fragment: Fragment, title: String){
            fragmentList.add(fragment)
            titleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titleList[position]
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }
    }
}
