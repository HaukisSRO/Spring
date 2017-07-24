@file:Suppress("DEPRECATION")

package sk.haukis.spring.commons

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.content.ContentValues.TAG
import android.hardware.Camera.PictureCallback
import java.lang.Exception


/**
 * Created by Daniel on 18. 7. 2017.
 */

class CameraView : SurfaceView, SurfaceHolder.Callback2{
    override fun surfaceRedrawNeeded(p0: SurfaceHolder?) {
        Log.e("Pojebana kamera", "redraw")
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        if (mHolder.surface == null) {
            // preview surface does not exist
            return
        }

        // stop preview before making changes
        try {
            camera.stopPreview()
        } catch (e: Exception) {
            Log.e("Pojebana kamera", e.message)
        }

        try {
            camera.setPreviewDisplay(mHolder)
            camera.startPreview()

        } catch (e: Exception) {
            Log.d(TAG, "Error starting camera preview: " + e.message)
        }

    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        Log.e("Surface", "Destroyed")
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        try {
            camera.setPreviewDisplay(mHolder)
            camera.startPreview()
        }
        catch (e: Exception){
            Log.e("Pojebana kamera", e.message)
        }
    }

    var mHolder: SurfaceHolder
    var camera : Camera

    constructor(context: Context, camera: Camera) : super(context){
        this.camera = camera
        mHolder = holder
        mHolder.addCallback(this)
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        camera.setDisplayOrientation(90)
    }

    fun takePicture(callback: PictureCallback) {
        camera.takePicture(null, null, callback)
    }

    fun restartCamera(){
        camera.stopPreview()
        camera.startPreview()
    }
}