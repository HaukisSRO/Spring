package sk.haukis.spring


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
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

    var listener: LoginListener? = null

    lateinit var springApi: SpringApi

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_login)
        springApi = SpringApi(activity)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        create_user.setOnClickListener {
            listener?.openRegistration()
        }

        loginButton.setOnClickListener{
            val email = emailET.text.toString()
            val password = passwordET.text.toString()
            val loginCall = springApi.getAccessToken(email, password)
            loginCall.enqueue(object : Callback<AccessToken>{
                override fun onResponse(call: Call<AccessToken>?, response: Response<AccessToken>) {
                    if (response.code() == 200) {
                        val accessToken: AccessToken = response.body()!!
                        val sp: SharedPreferences = activity.getSharedPreferences("Spring_app", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sp.edit()
                        editor.putString("ACCESS_TOKEN", "bearer ${accessToken.token}")
                        editor.apply()
                        listener?.OnLogin()
                    }
                    else {
                        Snackbar.make(activity.findViewById(android.R.id.content), getString(R.string.bad_login), Snackbar.LENGTH_SHORT).show()
                    }
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
            listener = context
    }

    interface LoginListener{
        fun OnLogin()
        fun openRegistration()
    }

    fun LogOut() {
        val sp : SharedPreferences = activity.getSharedPreferences("Spring_app", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sp.edit()
        editor.remove("ACCESS_TOKEN")
        editor.apply()
    }

}// Required empty public constructor
