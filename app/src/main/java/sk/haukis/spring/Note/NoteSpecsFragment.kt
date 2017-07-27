package sk.haukis.spring.Note


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_note_specs.*

import sk.haukis.spring.R


/**
 * A simple [Fragment] subclass.
 */
class NoteSpecsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_note_specs, container, false)
    }

    fun isPublic() : Boolean{
        return is_public.isChecked
    }

}// Required empty public constructor
