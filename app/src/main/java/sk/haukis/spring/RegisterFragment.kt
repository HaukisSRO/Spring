package sk.haukis.spring


import android.content.Context
import android.net.nsd.NsdManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.haukis.spring.API.SpringApi


/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {

    var listener : RegisterListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerButton.setOnClickListener {
            if (passwordET.text.toString() != passwordConfirmET.text.toString()){
                passwordConfirmET.error = getString(R.string.pass_match)
            }
            else {
                val springApi = SpringApi(activity)
                val registerCall = springApi.registerUser(emailET.text.toString(), passwordET.text.toString())
                registerCall.enqueue(object : Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                        listener?.onRegister()
                    }

                })
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is RegisterListener)
            listener = context
    }

    interface RegisterListener {
        fun onRegister ()
    }

}// Required empty public constructor
