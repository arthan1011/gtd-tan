package org.arthan.kotlin.gtd.web.rest

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import org.apache.commons.lang3.RandomStringUtils
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * Created by arthan on 20.08.2017. | Project gtd-tan
 */

val parser = Gson()
val jsonParser = JsonParser()

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

fun randomName(): String = RandomStringUtils.randomAlphanumeric(8)

fun utcInstant(year: Int, month: Int, day: Int, hour: Int): Instant {
	return ZonedDateTime.of(year, month, day, hour, 0, 0, 0, ZoneOffset.UTC).toInstant()
}



data class UserForTests(
		val username: String = randomName(),
		val password: String = randomName(),
		val userId: Long)