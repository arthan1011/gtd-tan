package org.arthan.kotlin.gtd.web.rest

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken

/**
 * Created by arthan on 20.08.2017. | Project gtd-tan
 */

val parser = Gson()
val jsonParser = JsonParser()

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)