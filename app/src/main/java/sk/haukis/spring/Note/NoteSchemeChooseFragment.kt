package sk.haukis.spring.Note


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_note_scheme_choose.*
import sk.haukis.spring.API.DB
import sk.haukis.spring.R
import sk.haukis.spring.Template.TemplateCreateActivity
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.RecyclerView
import sk.haukis.spring.Template.TemplateAdapter


/**
 * A simple [Fragment] subclass.
 */
class NoteSchemeChooseFragment : Fragment() {

    var chooseListener : OnTemplateChooseListener? = null
    lateinit var templatesAdapter : TemplateAdapter
    val db = DB()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        db.Init(activity)
        return inflater!!.inflate(R.layout.fragment_note_scheme_choose, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        templates_list.setHasFixedSize(true)
        templates_list.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        val templates = db.GetAllTemplates()
        templatesAdapter = TemplateAdapter(context, templates) {
            chooseListener?.onTemplateChoose(it.id)
        }

        templates_list.adapter = templatesAdapter

        create_new.setOnClickListener {
            val intent = Intent(activity, TemplateCreateActivity::class.java)
            startActivity(intent)
        }


        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                templates.removeAt(viewHolder.adapterPosition)
                templatesAdapter.notifyItemRemoved(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(templates_list)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnTemplateChooseListener)
            chooseListener = context
    }

    interface OnTemplateChooseListener {
        fun onTemplateChoose(id: Int)
    }

}// Required empty public constructor
