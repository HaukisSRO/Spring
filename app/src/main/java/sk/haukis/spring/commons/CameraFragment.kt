@file:Suppress("DEPRECATION")

package sk.haukis.spring.commons


import android.content.Context
import android.hardware.Camera
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_camera.*
import sk.haukis.spring.R
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.content.ContentValues.TAG
import android.R.attr.data
import android.net.Uri
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import android.R.attr.data
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Matrix


/**
 * A simple [Fragment] subclass.
 */
class CameraFragment : Fragment() {

    var camera : Camera? = null
    lateinit var cameraView : CameraView
    var listener : MediaCreateListener? = null
    lateinit var NoteId : String

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openCamera()
        cameraView = CameraView(context, camera!!)
        take_photo.setOnClickListener {
            takePhoto()
        }

        NoteId = arguments.getString("note_id")

        camera_holder.addView(cameraView)
    }

    fun openCamera() {
        try {
            camera = Camera.open()
        }
        catch (e: Exception){
            Log.e("CameraFragment", "Je to v piči ${e.localizedMessage}")
        }
    }

    fun takePhoto(){
        val callback = Camera.PictureCallback({ bytes, _ ->
            val file = getOutputMediaFile()
            if (file == null)
                Log.e("file", "is null")

            val options = BitmapFactory.Options()
            options.inMutable = true
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            val matrix = Matrix()
            matrix.postRotate(90F)
            val rotatedBitmap = Bitmap.createBitmap(bmp , 0, 0, bmp.width, bmp.height, matrix, true);

            try {
                val fos = FileOutputStream(file)
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
            } catch (e: FileNotFoundException) {
                Log.d(TAG, "File not found: " + e.message)
            } catch (e: IOException) {
                Log.d(TAG, "Error accessing file: " + e.message)
            }
            cameraView.restartCamera()
            listener?.onPhotoCreated(Uri.parse(file?.absolutePath))
        })

        cameraView.takePicture(callback)
    }

    private fun getOutputMediaFile(): File? {

        val mediaStorageDir = File(
                Environment.getExternalStorageDirectory().absolutePath
                        + "/Android/Data/"
                        + "sk.haukis.spring"
                        + "/Files/$NoteId/Images")

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Ach...", "failed to create directory")
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mediaFile: File
            mediaFile = File(mediaStorageDir.path + File.separator +
                    timeStamp + ".jpg")

        return mediaFile
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MediaCreateListener)
            listener = context
    }

    interface MediaCreateListener {
        fun onPhotoCreated(uri: Uri)
    }

}// Required empty public constructor
