package sk.haukis.spring.Note


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.design.widget.BottomNavigationView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.VideoView
import kotlinx.android.synthetic.main.fragment_note_media.*
import sk.haukis.spring.R


/**
 * A simple [Fragment] subclass.
 */
class NoteMediaFragment : Fragment() {

    var listener : OpenCameraListener? = null
    lateinit var NoteId : String

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        NoteId = arguments.getString("noteId")
        return inflater!!.inflate(R.layout.fragment_note_media, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottom_camera_chooser.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.create_media) {
                listener?.onOpenCamera(1)
                return@OnNavigationItemSelectedListener true
            } else if (item.itemId == R.id.select_media){
                listener?.onMediaPicker()
                return@OnNavigationItemSelectedListener true
            }
            false
        })
    }

    fun addImage(filePath: String){
        val imageView : ImageView = ImageView(context)
        val p = LinearLayout.LayoutParams(300, 300)
        imageView.layoutParams = p
        imageView.setImageURI(Uri.parse(filePath))
        photos_wrapper.addView(imageView)
    }

    fun addVideo(uri: Uri){
        val videoView : VideoView = VideoView(context)
        val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        videoView.layoutParams = p
        videoView.setVideoURI(uri)
        videos_wrapper.addView(videoView)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OpenCameraListener)
            listener = context
    }

    interface OpenCameraListener {
        fun onOpenCamera(method: Int)
        fun onMediaPicker()
    }

}// Required empty public constructor
