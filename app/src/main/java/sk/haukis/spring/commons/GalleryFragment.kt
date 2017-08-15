package sk.haukis.spring.commons


import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_gallery.*

import sk.haukis.spring.R


/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : Fragment() {

    fun newInstance(images: ArrayList<String>, online : Boolean = false) : GalleryFragment {
        val bundle = Bundle()
        bundle.putStringArrayList("images", images)
        bundle.putBoolean("online", online)

        val imageFragment = GalleryFragment()
        imageFragment.arguments = bundle
        return imageFragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_gallery, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = SectionsPagerAdapter(childFragmentManager)

        for (image in arguments.getStringArrayList("images")){
            adapter.addPage(ImageFragment().newInstance(image, arguments.getBoolean("online")))
        }
        container.adapter = adapter
    }

    class ImageFragment : Fragment(){

        fun newInstance(imageUri: String, online : Boolean = false) : ImageFragment{
            val bundle = Bundle()
            bundle.putString("image", imageUri)
            bundle.putBoolean("online", online)

            val imageFragment = ImageFragment()
            imageFragment.arguments = bundle
            return imageFragment
        }

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val image = ImageView(activity)
            image.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            if (arguments.getBoolean("online"))
                image.load("http://haukis-001-site6.etempurl.com/api/images/${arguments.getString("image")}/image")
            else
                image.setImageURI(Uri.parse(arguments.getString("image")))
            return image
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        val fragmentList : ArrayList<Fragment> = ArrayList()

        fun addPage(fragment: Fragment){
            fragmentList.add(fragment)
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }
    }
}// Required empty public constructor
