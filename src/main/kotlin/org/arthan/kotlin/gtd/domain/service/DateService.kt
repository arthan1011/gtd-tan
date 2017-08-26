package org.arthan.kotlin.gtd.domain.service

import org.springframework.stereotype.Service
import java.time.*
import java.util.*
import javax.inject.Singleton

/**
 * Created by arthan on 12.08.2017. | Project gtd-tan
 */

@Service
class DateService {

	var instant: Instant = Instant.now()

    fun getLocalDate(): LocalDate {
        return LocalDate.now()
    }

	fun setTimeInstant(newInstant: Instant) {
		instant = newInstant
	}

	fun getDay(offset: Int): LocalDate {
		val zonedDateTime = instant.atZone(ZoneOffset.ofHours(offset))
		return zonedDateTime.toLocalDate()
	}
}