package org.arthan.kotlin.gtd.domain.service

import org.arthan.kotlin.gtd.domain.model.DailyTask
import org.arthan.kotlin.gtd.domain.model.TaskState
import org.arthan.kotlin.gtd.domain.model.enums.TaskDateState
import org.arthan.kotlin.gtd.domain.repository.DailyTaskRepository
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.web.rest.dto.DateDTO
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

/**
 * Unit Test for TaskService
 * Created by shamsiev on 22.08.2017 for gtd-tan.
 */
class TaskServiceUnitTest {


	private lateinit var taskService: TaskService

	@Before
	fun setUp() {
		taskService = Mockito.spy(TaskService(
				Mockito.mock(DailyTaskRepository::class.java),
				Mockito.mock(DateService::class.java),
				Mockito.mock(UserRepository::class.java)))
	}

	@Test
	fun shouldReturnIncompleteStateForToday() {
		val completeState = taskService.isCompleted(true, LocalDate.now(), LocalDate.now())
		assertNull("Should be in incomplete state", completeState)
	}

	@Test
	fun shouldBeInFailedStateForYesterday() {
		val daysOffset: Long = 2
		val startLocal = dateWithOffset(daysOffset)
		val completed = taskService.isCompleted(false, startLocal, dateWithOffset(1))
		assertNotNull("Should be in failed state", completed)
		assertFalse("Should be in failed state", completed!!)
	}

	@Test
	fun shouldBeInFailedStateInTaskCreationDate() {
		val daysOffset: Long = 2
		val startLocal = dateWithOffset(daysOffset)
		val completed = taskService.isCompleted(false, startLocal, dateWithOffset(daysOffset))
		assertNotNull("Should be in failed state", completed)
		assertFalse("Should be in failed state", completed!!)
	}

	@Test
	fun shouldBeInIncompleteStateBeforeCreationDate() {
		val daysOffset: Long = 2
		val startLocal = dateWithOffset(daysOffset)
		val completed = taskService.isCompleted(false, startLocal, dateWithOffset(3))
		assertNull("Should be in incomplete state", completed)
	}

	@Test
	fun shouldReturnStateForCompletedTasks() {
		Mockito.doReturn(listOf(
				DailyTask(34, 1, "first", LocalDate.of(2015, 6, 7)),
				DailyTask(35, 1, "second", LocalDate.of(2015, 6, 7))
		)).`when`(taskService).findByUsername(Mockito.anyString())

		val firstDay = LocalDate.of(2015, 6, 8)
		val secondDay = LocalDate.of(2015, 6, 9)
		Mockito.doReturn(listOf(
				firstDay,
				secondDay
		)).`when`(taskService).createDates(Mockito.anyInt(), Mockito.anyInt())

		Mockito.doReturn(listOf(
				TaskState(id = 1, taskId = 34, date = firstDay, state = TaskDateState.COMPLETED),
				TaskState(id = 2, taskId = 35, date = secondDay, state = TaskDateState.COMPLETED)
		)).`when`(taskService).getTaskStates(from = firstDay, to = secondDay)

		val dateLineDates = taskService.getDateLineDates(0, "test_user", 0)

		/*         08.06.2015	09.06.2015
		task 34 -> 		true  		false
		task 35 -> 		false 		true
		*/
		assertNotNull("task 34 should be not Null for first day", dateLineDates[0].tasks[0].completed)
		assertTrue("task 34 should be completed for first day", dateLineDates[0].tasks[0].completed!!)
		assertNotNull("task 35 should be not Null for first day", dateLineDates[0].tasks[1].completed)
		assertFalse("task 35 should be completed for first day", dateLineDates[0].tasks[1].completed!!)
		assertNotNull("task 34 should be not Null for second day", dateLineDates[1].tasks[0].completed)
		assertFalse("task 34 should be completed for second day", dateLineDates[1].tasks[0].completed!!)
		assertNotNull("task 35 should be not Null for second day", dateLineDates[1].tasks[1].completed)
		assertTrue("task 35 should be completed for second day", dateLineDates[1].tasks[1].completed!!)
	}

	private fun dateWithOffset(daysOffset: Long) = LocalDate.now().minusDays(daysOffset)
}