package sk.haukis.spring.Note

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.note_item.view.*
import sk.haukis.spring.Models.Note
import sk.haukis.spring.R
import sk.haukis.spring.commons.inflate

/**
 * Created by danie_000 on 4.7.2017.
 */

class NotesAdapter(val activity: Activity, val notes: ArrayList<Note>, val longClickListener: (Note, Int) -> Unit) : RecyclerView.Adapter<NotesAdapter.ViewHolder>(){


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(notes[position], longClickListener)

    override fun getItemCount(): Int = notes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(activity, parent.inflate(R.layout.note_item))

    fun AddNote(note : Note){
        notes.add(note)
        notifyDataSetChanged()
        Log.e("TAG", "item added ${note.name}")
    }

    fun removeAt(position: Int) {
        notes.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, notes.size)
    }

    fun Update(notesList: ArrayList<Note>){
        notes.clear()
        notes.addAll(notesList)
        notifyDataSetChanged()
    }

    class ViewHolder(val activity: Activity, itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(item: Note, listener: (Note, Int) -> Unit) = with(itemView) {
            note_text.text = item.name

            if (item.titleImage != "")
                note_image.setImageURI(Uri.parse(item.titleImage))

            setOnClickListener {
                val p1: android.util.Pair<View, String> = android.util.Pair.create(itemView.note_image, "titleImage")
                val p2: android.util.Pair<View, String> = android.util.Pair.create(activity.findViewById(R.id.app_bar_layout), "app_bar_layout")
                //var options: ActivityOptionsCompat = ActivityOptionsCompat.
                //        makeSceneTransitionAnimation(context as Activity, p1)

                val intent = Intent(context, NoteDetailsActivity::class.java)
                intent.putExtra("note_id", item.id)
                Log.e("Itemid", item.id)
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context as Activity, p2).toBundle())
            }

            setOnLongClickListener {
                listener(item, adapterPosition)
                ViewCompat.setElevation(itemView.rootView, 20f)
                return@setOnLongClickListener true
            }

        }
    }

}