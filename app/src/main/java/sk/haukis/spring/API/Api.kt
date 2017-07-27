package sk.haukis.spring.API

import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import sk.haukis.spring.Models.AccessToken
import sk.haukis.spring.Models.Note
import sk.haukis.spring.Models.Template

/**
 * Created by danie_000 on 4.7.2017.
 */

interface Api {
    //Notes
    @GET("notes")
    fun allNotes(): Call<ArrayList<Note>>

    @GET("notes/{id}")
    fun getNote(@Path("id") id : String): Call<Note>

    @POST("notes")
    fun createNote(@Body note: Note): Call<Note>

    @GET("notes/public")
    fun getPublicNotes(): Call<ArrayList<Note>>

    @Multipart
    @POST("notes/{id}/images")
    fun addImages(@Path("id") id : String,
                  @Part images:  ArrayList<MultipartBody.Part>) : Call<ResponseBody>

    @DELETE("notes/{id}")
    fun deleteNote(@Path("id") id: String): Call<Note>


    //Templates
    @GET("templates")
    fun getTemplates(): Call<ArrayList<Template>>

    @POST("templates")
    fun createTemplate(@Body template: Template) : Call<Template>



    //User
    @FormUrlEncoded
    @POST("AccountApi/token")
    fun getAccessToken(@Field("email") email: String, @Field("password") password: String): Call<AccessToken>

    @FormUrlEncoded
    @POST("AccountApi")
    fun registerUser(@Field("email") email: String, @Field("password") password: String): Call<ResponseBody>

}