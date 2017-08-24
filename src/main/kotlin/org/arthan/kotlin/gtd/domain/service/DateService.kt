package org.arthan.kotlin.gtd.domain.service

import org.springframework.stereotype.Service
import java.time.*
import java.util.*

/**
 * Created by arthan on 12.08.2017. | Project gtd-tan
 */

@Service
class DateService {

	var instant: Instant = Instant.now()

    fun getLocalDate(): LocalDate {
        return LocalDate.now()
    }

	fun getDate(): Date {
		val zoneId = ZoneId.systemDefault()
		val date = Date.from(getLocalDate().atStartOfDay(zoneId).toInstant())
		return date
	}

	fun setTimeInstant(newInstant: Instant) {
		instant = newInstant
	}

	fun getDay(offset: Int): LocalDate? {
		val zonedDateTime = ZonedDateTime.now(ZoneOffset.ofHours(offset))
		return zonedDateTime.toLocalDate()
	}
}