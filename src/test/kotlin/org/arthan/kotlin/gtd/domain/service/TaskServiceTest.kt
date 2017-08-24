package org.arthan.kotlin.gtd.domain.service

import org.arthan.kotlin.gtd.GtdTanApplication
import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.DailyTaskRepository
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.web.rest.randomName
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Integration Test for TaskService
 * Created by shamsiev on 24.08.2017 for gtd-tan.
 */

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(GtdTanApplication::class))
class TaskServiceTest {

	companion object {
		private val USERNAME_1: String = randomName()
		private val PASSWORD_1: String = randomName()
		private val initialized: AtomicBoolean = AtomicBoolean(false)
	}

	@Autowired
	lateinit var userRepo: UserRepository
	@Autowired
	lateinit var dateService: DateService
	@Autowired
	lateinit var taskRepo: DailyTaskRepository

	@Before
	fun setUp() {
		if (!initialized.get()) {
			initUsers()
			initialized.set(true)
		}
	}

	private fun initUsers() {
		userRepo.save(User(USERNAME_1, PASSWORD_1, "USER", true))
	}

	@Autowired
	lateinit var taskService: TaskService

	@Test
	fun shouldCreateTaskWithStartDateBasedOnTimeOffset() {
		val year = 2017
		val month = 8
		val day = 24
		dateService.setTimeInstant(utcInstant(year, month, day, 6))
		val offset = 0
		val dailyTaskId = taskService.createDailyTask("first_test_task", USERNAME_1, offset)
		val dailyTask = taskRepo.findOne(dailyTaskId)
		val taskStartDate: LocalDate = dailyTask.startDate!!
		Assert.assertEquals("Should save correct date", LocalDate.of(year, month, day), taskStartDate)
	}

	private fun utcInstant(year: Int, month: Int, day: Int, hour: Int): Instant {
		return ZonedDateTime.of(year, month, day, hour, 0, 0, 0, ZoneOffset.UTC).toInstant()
	}
}