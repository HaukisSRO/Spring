package sk.haukis.spring.API

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sk.haukis.spring.Models.AccessToken
import sk.haukis.spring.Models.Note
import okhttp3.logging.HttpLoggingInterceptor
import sk.haukis.spring.Models.Template
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Created by danie_000 on 4.7.2017.
 */
class SpringApi constructor(activity : Activity? = null) {

    val api : Api

    init {
        if (activity != null) {
            val sharedPreferences: SharedPreferences = activity.getSharedPreferences("Spring_app", Context.MODE_PRIVATE)
            val accessToken: String = sharedPreferences.getString("ACCESS_TOKEN", "")

            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient().newBuilder()
                    .addInterceptor(AuthenticationInterceptor(accessToken))
                    .addInterceptor(logInterceptor)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build()

            val retrofit = Retrofit.Builder()
                    .baseUrl("http://haukis-001-site6.etempurl.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

            api = retrofit.create(Api::class.java)
        }
        else {
            val retrofit = Retrofit.Builder()
                    .baseUrl("http://haukis-001-site6.etempurl.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            api = retrofit.create(Api::class.java)
        }
    }

    fun getAccessToken (email : String, password : String) : Call<AccessToken> {
        return api.getAccessToken(email, password)
    }

    fun registerUser (email: String, password: String) : Call<ResponseBody> {
        return api.registerUser(email, password)
    }

    fun getAllNotes () : Call<ArrayList<Note>> {
        return api.allNotes()
    }

    fun getPublicNotes () : Call<ArrayList<Note>> {
        return api.getPublicNotes()
    }

    fun getNote (id : String) : Call<Note> {
        return api.getNote(id)
    }

    fun createNote(note: Note) : Call<Note> {
        val a = api.createNote(note)
        return a
    }

    fun addImages(noteId : String, images : ArrayList<Uri>) : Call<ResponseBody> {
        val files : ArrayList<File> = ArrayList()
        images.mapTo(files) { File(it.toString()) }

        val multipartImages : ArrayList<MultipartBody.Part> = ArrayList()
        files.mapTo(multipartImages) {
            val imageBody : RequestBody = RequestBody.create(MediaType.parse("image/*"), it)
            MultipartBody.Part.createFormData("images", it.name, imageBody)
        }

        return api.addImages(noteId, multipartImages)
    }

    fun deleteNote (id: String): Call<Note>{
        return api.deleteNote(id)
    }

    fun getTemplates(): Call<ArrayList<Template>> {
        return api.getTemplates()
    }

    fun createTemplate(template: Template): Call<Template> {
        return  api.createTemplate(template)
    }
}

class AuthenticationInterceptor(private val accessToken: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Log.e("Interceptor", "ACCESS TOKEN $accessToken")
        // Add authorization header with updated authorization value to intercepted request
        val authorisedRequest = originalRequest.newBuilder()
                .header("Authorization", accessToken)
                .build()
        return chain.proceed(authorisedRequest)
    }
}