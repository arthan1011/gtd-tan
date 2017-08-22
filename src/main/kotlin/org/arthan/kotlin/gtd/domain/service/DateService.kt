package org.arthan.kotlin.gtd.domain.service

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

/**
 * Created by arthan on 12.08.2017. | Project gtd-tan
 */

@Service
class DateService {
    fun getLocalDate(): LocalDate {
        return LocalDate.now()
    }

	fun getDate(): Date {
		val zoneId = ZoneId.systemDefault()
		val date = Date.from(getLocalDate().atStartOfDay(zoneId).toInstant())
		return date
	}
}