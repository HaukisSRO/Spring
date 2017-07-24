@file:Suppress("DEPRECATION")

package sk.haukis.spring.commons


import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_camera.*

import sk.haukis.spring.R


/**
 * A simple [Fragment] subclass.
 */
class CameraFragment : Fragment() {

    var camera : Camera? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openCamera()
        val cameraView = CameraView(context, camera!!)
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
}// Required empty public constructor
