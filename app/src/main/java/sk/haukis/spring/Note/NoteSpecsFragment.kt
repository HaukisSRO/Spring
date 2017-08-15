package sk.haukis.spring.Note


import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_note_specs.*
import sk.haukis.spring.Models.Note

import sk.haukis.spring.R
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import com.madrapps.pikolo.HSLColorPicker
import kotlinx.android.synthetic.main.fragment_note_specs.view.*


/**
 * A simple [Fragment] subclass.
 */
class NoteSpecsFragment : Fragment() {

    var pickedColor = ""

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_note_specs, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val colorPicker = view?.findViewById(R.id.colorPicker) as HSLColorPicker
        colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                pickedColor = String.format("#%06X", 0xFFFFFF and color)
                view.pickedColor.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            }
        })
    }

    fun Init (note: Note){
        is_public.isChecked = note.isPublic
    }

    fun isPublic() : Boolean{
        return is_public.isChecked
    }

}// Required empty public constructor
