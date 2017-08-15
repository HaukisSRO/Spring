package sk.haukis.spring

import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.util.Pair
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionInflater
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import co.metalab.asyncawait.async
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.haukis.spring.API.DB
import sk.haukis.spring.API.SpringApi
import sk.haukis.spring.Model.OfflineNote
import sk.haukis.spring.Models.Template
import sk.haukis.spring.Note.NoteCreateActivity
import sk.haukis.spring.Note.NotesFragment
import sk.haukis.spring.Template.TemplateCreateActivity
import sk.haukis.spring.commons.addFragment
import sk.haukis.spring.commons.removeFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NotesFragment.OnLoadListener, LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    lateinit var splashScreen : SplashFragment
    lateinit var myNotesFragment: NotesFragment
    lateinit var publicNotesFragment: NotesFragment
    val loginFragment: LoginFragment = LoginFragment()
    val registerFragment: RegisterFragment = RegisterFragment()
    val db = DB()

    override fun OnLogin() {
        this.removeFragment(loginFragment)
        InitView()
        tabs.setOnClickListener {
            Toast.makeText(this, "Click", Toast.LENGTH_LONG).show()
        }
    }

    override fun openRegistration() {
        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, registerFragment)
                .commit()
    }

    override fun onRegister() {
        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, loginFragment)
                .commit()
    }

    override fun OnLoad() {
        if (supportFragmentManager.fragments.contains(splashScreen))
            this.removeFragment(splashScreen)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)



        db.Init(this)
        //db.pultusOrm.drop(OfflineNote())

        splashScreen = SplashFragment()
        this.addFragment(splashScreen)

        val sp : SharedPreferences = getSharedPreferences("Spring_app", Context.MODE_PRIVATE)
        val accessToken = sp.getString("ACCESS_TOKEN", "")

        if (accessToken != ""){
            SyncNotes()
        }

        else {
            supportFragmentManager.beginTransaction()
                    .remove(splashScreen)
                    .add(android.R.id.content, loginFragment)
                    .addToBackStack("login")
                    .commit()
        }

        create_note.setOnClickListener {
            val intent = Intent(baseContext, NoteCreateActivity::class.java)
            val p1 : Pair<View, String> = Pair.create(app_bar_layout, "app_bar_layout")
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, p1).toBundle())
        }

        DownloadTemplates()

        val explode = TransitionInflater.from(this)
                .inflateTransition(R.transition.explode)
        window.exitTransition = explode

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    fun DownloadTemplates(){
        val self = this
        val springApi: SpringApi = SpringApi(self)
        if (springApi.isOnline()) {
            async {
                val templateCall = springApi.getTemplates()
                templateCall.enqueue(object : Callback<ArrayList<Template>> {

                    override fun onResponse(call: Call<ArrayList<Template>>?, response: Response<ArrayList<Template>>?) {
                        val templates = response?.body()

                        db.SaveAllTemplates(templates!!)
                    }

                    override fun onFailure(call: Call<ArrayList<Template>>?, t: Throwable?) {
                        Log.e("S", t?.message)
                    }
                })
            }
        }
    }

    fun SyncNotes(){
        val springApi = SpringApi(this)
        if (db.IsOfflineNote() && springApi.isOnline()){
            val offlineNotes = db.GetAllOfflineNotes()
            //offlineNotes.forEach({
            //    if (it.action == 0){
            //        it.note.Create(springApi)
            //    }
            //})
            val syncCall = springApi.syncNotes(offlineNotes)
            syncCall.enqueue(object: Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    InitView()
                    db.pultusOrm.drop(OfflineNote())
                }

            })
        }
        InitView()
    }

    fun InitView(){
        myNotesFragment = NotesFragment().newInstance(1)
        publicNotesFragment = NotesFragment().newInstance(2)
        val adapter = SectionsPagerAdapter(supportFragmentManager)
        adapter.addPage(myNotesFragment, "My")
        adapter.addPage(publicNotesFragment, "Public")
        container.adapter = adapter
        tabs.setupWithViewPager(container)
        GetPermissions()
    }

    fun GetPermissions(){
        val permissionsNeeded = ArrayList<String>()
        permissionsNeeded.add("android.permission.CAMERA")
        permissionsNeeded.add("android.permission.RECORD_AUDIO")
        permissionsNeeded.add("android.permission.WRITE_EXTERNAL_STORAGE")


        ActivityCompat.requestPermissions(this,
                permissionsNeeded.toTypedArray(),
                1)
    }

    fun addPermission (permission: String) : Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                return false
        }
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.new_note -> {
                val intent = Intent(baseContext, NoteCreateActivity::class.java)
                val p1 : Pair<View, String> = Pair.create(app_bar_layout, "app_bar_layout")
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, p1).toBundle())
            }
            R.id.nav_gallery -> {
                val intent = Intent(baseContext, TemplateCreateActivity::class.java)
                val p1 : Pair<View, String> = Pair.create(app_bar_layout, "app_bar_layout")
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, p1).toBundle())
            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.logout -> {
                val sp : SharedPreferences = getSharedPreferences("Spring_app", Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sp.edit()
                editor.remove("ACCESS_TOKEN")
                editor.apply()
                finishAfterTransition()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
}
