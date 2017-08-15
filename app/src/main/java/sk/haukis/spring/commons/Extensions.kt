package sk.haukis.spring.commons

import android.support.v4.app.Fragment
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.support.annotation.IdRes
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import retrofit2.Call
import android.net.NetworkInfo
import android.net.ConnectivityManager



fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun ImageView.load(url : String){
    Picasso.with(context).load(url).into(this)
}

fun FragmentActivity.removeFragment(fragment: Fragment, removeFromBackStack: Boolean = true){
    this.supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .remove(fragment)
            .commit()
    if (removeFromBackStack)
        this.supportFragmentManager.popBackStack()
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(fragment.view!!.getWindowToken(), 0)
}

fun FragmentActivity.addFragment(fragment: Fragment, addToBackStack: String?, @IdRes view: Int = android.R.id.content){
    this.supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .add(view, fragment)
            .addToBackStack(addToBackStack)
            .commit()
}

fun FragmentActivity.addFragment(fragment: Fragment, @IdRes view: Int = android.R.id.content){
    this.supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .add(view, fragment)
            .commit()
}

fun Bitmap.resize(width: Int) : Bitmap {
    val originalWidth = this.height
    val originalHeight = this.width

    val height =  originalHeight / (originalWidth / width)

    val background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(background)

    val scale = width.toFloat() / originalWidth


    val xTranslation = 0.0f
    val yTranslation = (height - originalHeight * scale)

    val transformation = Matrix()
    transformation.postTranslate(xTranslation, yTranslation)
    transformation.preScale(scale, scale)

    val paint = Paint()
    paint.isFilterBitmap = true

    canvas.drawBitmap(this, transformation, paint)

    return background
}
