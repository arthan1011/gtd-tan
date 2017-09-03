package org.arthan.kotlin.gtd.domain.service

import org.springframework.stereotype.Service
import java.time.*

/**
 * Created by arthan on 12.08.2017. | Project gtd-tan
 */

@Service
class DateService {

	private var clock: Clock = Clock.systemUTC()

	fun setTimeInstant(newInstant: Instant) {
		clock = Clock.fixed(newInstant, ZoneOffset.UTC)
	}

	fun getDay(offset: Int): LocalDate {
		val zonedDateTime = takeInstant().atZone(ZoneOffset.ofHours(offset))
		return zonedDateTime.toLocalDate()
	}

	fun takeInstant(): Instant = clock.instant()
}