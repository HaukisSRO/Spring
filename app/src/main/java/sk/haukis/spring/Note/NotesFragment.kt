package sk.haukis.spring.Note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.ActionMode
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.transition.*
import android.util.Log
import android.view.*
import co.metalab.asyncawait.async
import kotlinx.android.synthetic.main.fragment_notes.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.haukis.spring.API.DB
import sk.haukis.spring.API.SpringApi
import sk.haukis.spring.Model.OfflineNote
import sk.haukis.spring.Models.Note
import sk.haukis.spring.R
import sk.haukis.spring.commons.PropagatingTransition
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class NotesFragment : Fragment() {

    val MODE_PRIVATE = 1
    val MODE_PUBLIC = 2

    var mActionMode: ActionMode? = null
    lateinit var springApi : SpringApi
    lateinit var allNotes: ArrayList<Note>
    lateinit var notesAdapter : NotesAdapter
    private var LoadListener : OnLoadListener? = null
    var selectedNote: String = ""
    var selectedViewPosition: Int = 0
    val db : DB = DB()
    var mode : Int = 1



    fun newInstance(mode: Int) : NotesFragment{
        val bundle = Bundle()
        bundle.putInt("mode", mode)

        val imageFragment = NotesFragment()
        imageFragment.arguments = bundle
        return imageFragment
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        springApi = SpringApi(activity)
        val view = inflater?.inflate(R.layout.fragment_notes, container, false)
        db.Init(context)
        mode = arguments.getInt("mode")
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notes_list.setHasFixedSize(true)
        notes_list.layoutManager = GridLayoutManager(activity, 2)

        allNotes = db.GetAllNotes()
        LoadListener?.OnLoad()
        notesAdapter = NotesAdapter(activity, allNotes) { note: Note, position: Int ->
            Log.e("Long", "Click")
            mActionMode = activity.startActionMode(mActionModeCallback)
            selectedNote = note.id
            selectedViewPosition = position
        }
        notes_list.adapter = notesAdapter
        RefreshList()

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                deleteNote(allNotes[viewHolder.adapterPosition].id, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(notes_list)

        swipe_refresh_layout.setOnRefreshListener {
            RefreshList()
        }
    }

    fun Sync() {
        val offlineNotes = db.GetAllOfflineNotes()
        if (springApi.isOnline()){
            if (offlineNotes.size > 0) {
                val syncCall = springApi.syncNotes(offlineNotes)
                syncCall.enqueue(object : Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                        Log.e("TAG", "synced")
                        db.pultusOrm.drop(OfflineNote())
                        RefreshList()
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })
            }
            else {
                RefreshList()
            }
        }
        swipe_refresh_layout.isRefreshing = false
    }

    fun RefreshList(){
        async {
            if (springApi.isOnline()) {
                if (mode == MODE_PRIVATE) {
                    val notesCall = springApi.getAllNotes()
                    notesCall.enqueue(object : Callback<ArrayList<Note>> {
                        override fun onResponse(call: Call<ArrayList<Note>>, response: Response<ArrayList<Note>>) {
                            allNotes = response.body()!!
                            setUp()
                        }

                        override fun onFailure(call: Call<ArrayList<Note>>?, t: Throwable?) {
                            Log.e("TAG1", t?.message)
                        }

                    })
                } else if (mode == MODE_PUBLIC) {
                    val notesCall = springApi.getPublicNotes()
                    notesCall.enqueue(object : Callback<ArrayList<Note>> {
                        override fun onResponse(call: Call<ArrayList<Note>>, response: Response<ArrayList<Note>>) {
                            allNotes = response.body()!!
                            setUp()
                        }

                        override fun onFailure(call: Call<ArrayList<Note>>?, t: Throwable?) {
                            Log.e("TAG1", t?.message)
                        }
                    })
                }
            }
            else {
                allNotes = db.GetAllNotes()
                setUp()
            }

        }
    }

    fun setUp(){
        notesAdapter.Update(allNotes)

        if (mode == MODE_PRIVATE)
            db.SaveAllNotes(allNotes)

        notesAdapter.notifyDataSetChanged()
        swipe_refresh_layout.isRefreshing = false
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnLoadListener){
            LoadListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        LoadListener = null
    }

    interface OnLoadListener {
        fun OnLoad()
    }

    override fun onResume() {
        super.onResume()
        RefreshList()
    }


    private val mActionModeCallback: ActionMode.Callback by lazy {
        object : ActionMode.Callback {


            // Called when the action mode is created; startActionMode() was called
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                // Inflate a menu resource providing context menu items
                val inflater = mode.menuInflater
                inflater.inflate(R.menu.notes_context_menu, menu)
                return true
            }

            // Called each time the action mode is shown. Always called after onCreateActionMode, but
            // may be called multiple times if the mode is invalidated.
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false // Return false if nothing is done
            }

            // Called when the user selects a contextual menu item
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.note_delete -> {
                        if (selectedNote != "") {
                            deleteNote(selectedNote, selectedViewPosition)
                        }
                        mode.finish() // Action picked, so close the CAB
                        return true
                    }
                    R.id.note_edit -> {
                        if (selectedNote != ""){
                            val intent = Intent(context, NoteCreateActivity::class.java)
                            intent.putExtra("editMode", true)
                            intent.putExtra("noteId", selectedNote)
                            startActivity(intent)
                            mode.finish()
                        }
                        return true
                    }
                    R.id.note_share -> {
                        if (selectedNote != ""){
                            val i = Intent(Intent.ACTION_SEND)
                            i.type = "text/plain"
                            i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL")
                            i.putExtra(Intent.EXTRA_TEXT, "http://haukis-001-site6.etempurl.com/notes/details/$selectedNote")
                            startActivity(Intent.createChooser(i, "Share URL"))
                        }
                        return true
                    }
                    else -> return false
                }
            }

            // Called when the user exits the action mode
            override fun onDestroyActionMode(mode: ActionMode) {
                mActionMode = null
                selectedNote = ""
            }
        }
    }

    fun deleteNote(id: String, position: Int){
        Snackbar.make(activity.findViewById(R.id.coordinator), getString(R.string.delete_note), Snackbar.LENGTH_LONG).show()

        val deleteCall = springApi.deleteNote(id)
        deleteCall.enqueue(object: Callback<Note>{
            override fun onResponse(call: Call<Note>?, response: Response<Note>) {
                Log.e("Deleted", response.body().toString())
            }

            override fun onFailure(call: Call<Note>?, t: Throwable?) {
                val offlineNote = OfflineNote()
                offlineNote.id = UUID.randomUUID().toString()
                offlineNote.noteId = id
                offlineNote.action = offlineNote.DELETE
                db.Save(offlineNote)
            }
        })
        notesAdapter.removeAt(position)
        db.Delete(allNotes[position])
        allNotes.removeAt(position)
    }
}
