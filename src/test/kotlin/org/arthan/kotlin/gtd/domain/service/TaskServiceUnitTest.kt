package org.arthan.kotlin.gtd.domain.service

import org.arthan.kotlin.gtd.web.rest.dto.DateDTO
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

/**
 * Unit Test for TaskService
 * Created by shamsiev on 22.08.2017 for gtd-tan.
 */
class TaskServiceUnitTest {

	@Test
	fun shouldReturnIncompleteStateForToday() {
		val completeState = isCompleted(true, LocalDate.now(), LocalDate.now())
		assertNull("Should be in incomplete state", completeState)
	}

	@Test
	fun shouldBeInFailedStateForYesterday() {
		val daysOffset: Long = 2
		val startLocal = dateWithOffset(daysOffset)
		val completed = isCompleted(false, startLocal, dateWithOffset(1))
		assertNotNull("Should be in failed state", completed)
		assertFalse("Should be in failed state", completed!!)
	}

	@Test
	fun shouldBeInFailedStateInTaskCreationDate() {
		val daysOffset: Long = 2
		val startLocal = dateWithOffset(daysOffset)
		val completed = isCompleted(false, startLocal, dateWithOffset(daysOffset))
		assertNotNull("Should be in failed state", completed)
		assertFalse("Should be in failed state", completed!!)
	}

	@Test
	fun shouldBeInIncompleteStateBeforeCreationDate() {
		val daysOffset: Long = 2
		val startLocal = dateWithOffset(daysOffset)
		val completed = isCompleted(false, startLocal, dateWithOffset(3))
		assertNull("Should be in incomplete state", completed)
	}

	private fun dateWithOffset(daysOffset: Long) = LocalDate.now().minusDays(daysOffset)
}