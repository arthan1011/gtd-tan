package org.arthan.kotlin.gtd.domain.service

import org.arthan.kotlin.gtd.web.rest.dto.DateDTO
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

/**
 * Created by shamsiev on 22.08.2017 for gtd-tan.
 */
class TaskServiceTest {

	val zoneId: ZoneId = ZoneId.systemDefault()

	@Test
	fun shouldReturnIncompleteStateForToday() {
		val completeState = isCompleted(true, Date(), DateDTO())
		assertNull("Should be in incomplete state", completeState)
	}

	@Test
	fun shouldBeInFailedStateForYesterday() {
		val daysOffset: Long = 2
		val startLocal = dateWithOffset(daysOffset)
		val startDate = Date.from(startLocal.atStartOfDay(zoneId).toInstant())
		val completed = isCompleted(false, startDate, dateDTOWithOffset(1))
		assertNotNull("Should be in failed state", completed)
		assertFalse("Should be in failed state", completed!!)
	}

	@Test
	fun shouldBeInFailedStateInTaskCreationDate() {
		val daysOffset: Long = 2
		val startLocal = dateWithOffset(daysOffset)
		val startDate = Date.from(startLocal.atStartOfDay(zoneId).toInstant())
		val completed = isCompleted(false, startDate, dateDTOWithOffset(daysOffset))
		assertNotNull("Should be in failed state", completed)
		assertFalse("Should be in failed state", completed!!)
	}

	@Test
	fun shouldBeInIncompleteStateBeforeCreationDate() {
		val daysOffset: Long = 2
		val startLocal = dateWithOffset(daysOffset)
		val startDate = Date.from(startLocal.atStartOfDay(zoneId).toInstant())
		val completed = isCompleted(false, startDate, dateDTOWithOffset(3))
		assertNull("Should be in incomplete state", completed)
	}

	private fun dateDTOWithOffset(offset: Long): DateDTO {
		val local = dateWithOffset(offset)
		return DateDTO(
				day = local.dayOfMonth.toString(),
				month = local.monthValue.toString(),
				year = local.year.toString()
		)
	}

	private fun dateWithOffset(daysOffset: Long) = LocalDate.now().minusDays(daysOffset)
}