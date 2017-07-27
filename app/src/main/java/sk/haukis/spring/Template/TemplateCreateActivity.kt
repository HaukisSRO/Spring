package sk.haukis.spring.Template

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.google.gson.Gson
import sk.haukis.spring.R

import kotlinx.android.synthetic.main.activity_template_create.*
import kotlinx.android.synthetic.main.content_template_create.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.haukis.spring.API.SpringApi
import sk.haukis.spring.Models.Template

class TemplateCreateActivity : AppCompatActivity() {

    var parameterCount : ArrayList<EditText> = ArrayList()
    var parameters : ArrayList<Parameter> = ArrayList()
    lateinit var springApi : SpringApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_create)
        setSupportActionBar(toolbar)

        add_param.setOnClickListener {
            AddParam()
        }

        save_template.setOnClickListener {
            SaveTemplate()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        springApi = SpringApi(this)
    }

    fun AddParam(){
        val param = EditText(this)
        val paramHolder = android.support.design.widget.TextInputLayout(this)

        val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        param.layoutParams = p
        paramHolder.layoutParams = p
        param.id = parameterCount.size
        param.hint = "Parameter ${parameterCount.size +1}"

        parameterCount.add(param)
        paramHolder.addView(param)
        param_layout.addView(paramHolder)
        param.requestFocus()
    }

    fun SaveTemplate(){
        var scheme = ""
        for (et in parameterCount ){
            parameters.add(Parameter(et.id, et.text.toString()))
        }

        scheme = Gson().toJson(parameters)

        var template : Template = Template()
        template.name = param1.text.toString()

        template.scheme = scheme

        val templateCreateCall = springApi.createTemplate(template)
        templateCreateCall.enqueue(object: Callback<Template>{
            override fun onResponse(call: Call<Template>?, response: Response<Template>?) {
                Log.e("TemplateResponse", response?.body().toString())
            }

            override fun onFailure(call: Call<Template>?, t: Throwable?) {
                Log.e("TemplateResponse", t?.message)
            }
        })

        Log.e("TAG", scheme)
    }


    class Parameter constructor(id: Int, text: String) {
        var id : Int = id
        var text : String = text
    }
}
