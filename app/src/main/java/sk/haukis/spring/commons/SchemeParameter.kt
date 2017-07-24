package sk.haukis.spring.commons

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Daniel on 24. 7. 2017.
 */

class SchemeParameter constructor(var id: Int = 0, var text: String = ""){

    fun parse(schemeJson: String) : ArrayList<SchemeParameter>{
        val type = object : TypeToken<ArrayList<SchemeParameter>>(){}.type
        return Gson().fromJson<ArrayList<SchemeParameter>>(schemeJson, type)
    }
}