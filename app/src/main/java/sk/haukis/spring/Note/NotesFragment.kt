package sk.haukis.spring.Note

import android.content.Context
import android.os.Bundle
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.haukis.spring.API.DB
import sk.haukis.spring.API.SpringApi
import sk.haukis.spring.Models.Note
import sk.haukis.spring.R
import sk.haukis.spring.commons.PropagatingTransition


/**
 * A simple [Fragment] subclass.
 */
class NotesFragment : Fragment() {

    var mActionMode: ActionMode? = null
    lateinit var springApi : SpringApi
    lateinit var allNotes: ArrayList<Note>
    lateinit var notesAdapter : NotesAdapter
    private var LoadListener : OnLoadListener? = null
    var selectedNote: String = ""
    var selectedViewPosition: Int = 0
    val db : DB = DB()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        springApi = SpringApi(activity)
        val view = inflater?.inflate(R.layout.fragment_notes, container, false)
        db.Init(context)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notes_list.setHasFixedSize(true)
        notes_list.layoutManager = GridLayoutManager(activity, 2)

        allNotes = db.GetAllNotes()
        LoadListener?.OnLoad()
        notesAdapter = NotesAdapter(allNotes) { note: Note, position: Int ->
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

    fun RefreshList(){
        async {
            val notesCall = springApi.getAllNotes()
            notesCall.enqueue(object : Callback<ArrayList<Note>> {
                override fun onResponse(call: Call<ArrayList<Note>>, response: Response<ArrayList<Note>>) {
                    val allNotes1 = response.body()!!

                    allNotes = allNotes1
                    notesAdapter.Update(allNotes)

                    db.SaveAllNotes(allNotes)

                    notesAdapter.notifyDataSetChanged()
                    swipe_refresh_layout.isRefreshing = false
                    PropagatingTransition(sceneRoot = notes_list,
                            startingView = notes_list.getChildAt(0),
                            transition = TransitionSet()
                                    .addTransition(Fade(Fade.IN)
                                            .setInterpolator { (it - 0.5f) * 2 })
                                    .addTransition(Explode())
                    )
                            .start()
                }

                override fun onFailure(call: Call<ArrayList<Note>>?, t: Throwable?) {
                    Log.e("TAG1", t?.message)
                    swipe_refresh_layout.isRefreshing = false
                }

            })
        }
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
        val deleteCall = springApi.deleteNote(id)
        deleteCall.enqueue(object: Callback<Note>{
            override fun onResponse(call: Call<Note>?, response: Response<Note>) {
                Log.e("Deleted", response.body().toString())
                notesAdapter.removeAt(position)
                db.Delete(allNotes[position])
                allNotes.removeAt(position)
            }

            override fun onFailure(call: Call<Note>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}
