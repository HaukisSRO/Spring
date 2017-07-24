package sk.haukis.spring


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.haukis.spring.API.SpringApi
import sk.haukis.spring.Models.AccessToken
import sk.haukis.spring.commons.inflate


/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    var onLoginListener : LoginListener? = null

    lateinit var springApi: SpringApi

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_login)
        springApi = SpringApi(activity)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener{
            val email = emailET.text.toString()
            val password = passwordET.text.toString()
            val loginCall = springApi.getAccessToken(email, password)
            loginCall.enqueue(object : Callback<AccessToken>{
                override fun onResponse(call: Call<AccessToken>?, response: Response<AccessToken>) {
                    val accessToken : AccessToken = response.body()!!
                    val sp : SharedPreferences = activity.getSharedPreferences("Spring_app", Context.MODE_PRIVATE)
                    val editor : SharedPreferences.Editor = sp.edit()
                    editor.putString("ACCESS_TOKEN", "bearer ${accessToken.token}")
                    editor.apply()
                    onLoginListener?.OnLogin()
                }

                override fun onFailure(call: Call<AccessToken>?, t: Throwable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is LoginListener)
            onLoginListener = context
    }

    interface LoginListener{
        fun OnLogin()
    }

}// Required empty public constructor
