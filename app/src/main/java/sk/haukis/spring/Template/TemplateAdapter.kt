package sk.haukis.spring.Template

import android.content.Context
import android.support.v7.widget.RecyclerView
import sk.haukis.spring.Models.Template
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.template_item.view.*
import sk.haukis.spring.R
import sk.haukis.spring.commons.inflate


/**
 * Created by Daniel on 24. 7. 2017.
 */

class TemplateAdapter (val context : Context, val templates: ArrayList<Template>, val clickListener: (Template) -> Unit ) : RecyclerView.Adapter<TemplateAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.template_item))

    override fun getItemCount(): Int = templates.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int): Unit = holder.bind(templates[position], clickListener)


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind (item: Template, clickListener: (Template) -> Unit) = with(itemView) {
            template_name.text = item.name
            setOnClickListener { clickListener(item) }
        }
    }
}