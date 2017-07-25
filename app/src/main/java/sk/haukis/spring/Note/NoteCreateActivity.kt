package sk.haukis.spring.Note

import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_note_create.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.haukis.spring.API.SpringApi
import sk.haukis.spring.Models.Note

import sk.haukis.spring.R
import sk.haukis.spring.commons.CameraFragment
import sk.haukis.spring.commons.removeFragment
import sk.haukis.spring.commons.unwrapCall
import java.util.*

class NoteCreateActivity : AppCompatActivity(), NoteSchemeChooseFragment.OnTemplateChooseListener, NoteMediaFragment.OpenCameraListener {

    lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    val templateChooser : NoteSchemeChooseFragment = NoteSchemeChooseFragment()
    val noteMedia : NoteMediaFragment = NoteMediaFragment()
    val noteCreate : NoteCreateFragment = NoteCreateFragment()
    val camera : CameraFragment = CameraFragment()
    var NoteId = UUID.randomUUID().toString()
    lateinit var springApi : SpringApi

    override fun onTemplateChoose(id: Int) {
        tabs.visibility = View.VISIBLE
        container.visibility = View.VISIBLE
        noteCreate.Init(id)
        this.removeFragment(templateChooser)
    }

    override fun onOpenCamera(method: Int) {
        tabs.visibility = View.GONE
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

        supportFragmentManager.beginTransaction()
                .add(R.id.main_container, templateChooser)
                .commit()

        tabs.visibility = View.GONE
        container.visibility = View.INVISIBLE

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        val bundle = Bundle()
        bundle.putString("noteId", NoteId)
        noteMedia.arguments = bundle
        mSectionsPagerAdapter.addPage(noteCreate, "Template")
        mSectionsPagerAdapter.addPage(noteMedia, "Media")

        // Set up the ViewPager with the sections adapter.
        container!!.adapter = mSectionsPagerAdapter

        tabs.setupWithViewPager(container, true)

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        springApi = SpringApi(this)

        save_note.setOnClickListener {
            saveNote()
        }
    }

    fun saveNote(){
        val note = noteCreate.getNote()
        note.id = NoteId
        val noteSaveCall = springApi.createNote(note)
        noteSaveCall.enqueue(object: Callback<Note> {
            override fun onFailure(call: Call<Note>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<Note>?, response: Response<Note>?) {
                Log.e("NoteCreate", "Saved ${note.name}")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_note_create, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

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

    override fun onResume() {
        super.onResume()
        NoteId = UUID.randomUUID().toString()
    }
}
