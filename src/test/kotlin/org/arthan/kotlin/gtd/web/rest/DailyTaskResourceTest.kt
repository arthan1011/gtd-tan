package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.domain.service.DateService
import org.arthan.kotlin.gtd.domain.service.TaskService
import org.arthan.kotlin.gtd.web.rest.DailyTaskResourceTest.DateLineStateMatcher.Companion.matches
import org.arthan.kotlin.gtd.web.rest.dto.*
import org.arthan.kotlin.gtd.web.rest.resolver.TIME_OFFSET_HEADER
import org.hamcrest.Description
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by arthan on 20.08.2017. | Project gtd-tan
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="administrator",roles= arrayOf("USER","ADMIN"))
@ActiveProfiles(profiles = arrayOf("test"))
class DailyTaskResourceTest {

	companion object {
        private val initialized: AtomicBoolean = AtomicBoolean(false)
		private val FUTURE_DATES_NUMBER = 14
	}

    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var userRepo: UserRepository
	@Autowired
	lateinit var taskService: TaskService

    @Before
    fun setUp() {
        if (!initialized.get()) {
            initUsers()
            initialized.set(true)
        }
    }

    private fun initUsers() {
        userRepo.save(User("administrator", "password", "ADMIN", enabled = true))
    }

    @Test
    fun shouldCreateAndRetrieveOneTask() {
		val user = createUser()
		val name = "test_task"
        val taskDTO = NewTaskDTO(name)
        val postRequest = post("/rest/task/daily")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
				.header("AX-GTD-User-ID", user.userId)
				.header(TIME_OFFSET_HEADER, 180)
				.content(parser.toJson(taskDTO))

        val getRequest = get("/rest/task/daily")
				.header("AX-GTD-User-ID", user.userId)
				.header(TIME_OFFSET_HEADER, 180)

		// retrieve initial task list size
        var tasksSizeBefore = -1
        mockMvc.perform(getRequest)
				.andExpect(MockMvcResultMatchers.status().isOk)
                .andDo {
                    val string = it.response.contentAsString
                    tasksSizeBefore = jsonParser.parse(string).asJsonObject["tasks"].asJsonArray.size()
                }

        // add one task
        mockMvc.perform(postRequest)
                .andExpect(status().isOk)

        // check new task was correctly added
        var dailyData: DailyDTO? = null
        mockMvc.perform(getRequest)
                .andDo { dailyData = parser.fromJson(it.response.contentAsString) }

        val tasksAfter = dailyData!!.tasks
        assertEquals("One new task was not added", tasksSizeBefore + 1, tasksAfter.size)
        assertNotEquals("New task did not have correct id", -1, tasksAfter.first().id)
    }

	@Test
	fun retrievedDateLineItemsShouldHaveCorrectDates() {
		val testUser: UserForTests = createUser()
		val firstTaskName = "test_task_1"
		val firstMvcResult = mockMvc.perform(post("/rest/task/daily")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.header(TIME_OFFSET_HEADER, 180)
				.header("AX-GTD-User-ID", testUser.userId)
				.with(SecurityMockMvcRequestPostProcessors.user(testUser.username).password(testUser.password))
				.content(parser.toJson(NewTaskDTO(firstTaskName))))
				.andExpect(status().isOk)
				.andReturn()
		val firstTaskId = jsonParser.parse(firstMvcResult.response.contentAsString).asJsonObject["id"].asLong
		val dataLineItems: List<DatelineItemDTO> = retrieveDateLineItems(testUser, 180)
		assertEquals(
				"Incorrect number of datelineItems was retrieved",
				DailyTaskResource.DATE_LINE_ITEMS_SIZE,
				dataLineItems.size)

		val todayDateItem: DatelineItemDTO = dataLineItems.find { it.today }!!
		val date = LocalDate.now()

		assertEquals("last dateline item should be this year", date.year, todayDateItem.date.year)
		assertEquals("last dateline item should be this month", date.monthValue, todayDateItem.date.month)
		assertEquals("last dateline item should be this day", date.dayOfMonth, todayDateItem.date.day)

		val allItemsHaveExactlyOneTask = dataLineItems.all { it.tasks.size == 1 }
		val allItemsHaveTaskWithSavedId = dataLineItems.all { it.tasks.first().id == firstTaskId }

		assertTrue("All items should have exactly one task", allItemsHaveExactlyOneTask)
		assertTrue("All items should have task with saved id", allItemsHaveTaskWithSavedId)
	}

	private fun createUser(): UserForTests {
		val username = randomName()
		val password = randomName()
		val savedUser = userRepo.save(User(username, password, "USER"))
		return UserForTests(username, password, savedUser.id)
	}

	@Test
	fun dateLineItemsStatusShouldBeFailedForTasksWithStartDateInPast() {
		val testUser: UserForTests = createUser()
		val nowDate = LocalDate.now()
		val firstTaskName = "task_task_1"
		val secondTaskName = "task_task_2"
		val dateService = DateService()
		val spyDateService = spy(dateService)
		taskService.dateService = spyDateService

		mockMvc.perform(
				post("/rest/task/daily")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.header("AX-GTD-User-ID", testUser.userId)
						.header(TIME_OFFSET_HEADER, 180)
						.content(parser.toJson(NewTaskDTO(firstTaskName))))
				.andExpect(status().isOk)

		// set date to 2 days before
		doReturn(nowDate.minusDays(2)).`when`(spyDateService).getDay(Mockito.anyInt())
		val secondMvcResult = mockMvc.perform(
				post("/rest/task/daily")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.header("AX-GTD-User-ID", testUser.userId)
						.header(TIME_OFFSET_HEADER, 180)
						.content(parser.toJson(NewTaskDTO(secondTaskName))))
				.andExpect(status().isOk)
				.andReturn()
		val secondTaskId = jsonParser.parse(secondMvcResult.response.contentAsString).asJsonObject["id"].asLong

		taskService.dateService = DateService()
		val dataLineItems: List<DatelineItemDTO> = retrieveDateLineItems(testUser, 180)

		assertEquals("date items should have two tasks", 2, dataLineItems.first().tasks.size)
		assertEquals("tasks should be in correct order", secondTaskId, dataLineItems.first().tasks[1].id)
		assertTrue(
				"tasks for today should be in incomplete state",
				dataLineItems.last().tasks.all { it.completed == null })
		assertFalse(
				"second task for yesterday should be in failed state",
				dataLineItems[dataLineItems.lastIndex - 1 - FUTURE_DATES_NUMBER].tasks[1].completed!!)
		assertNull(
				"first task for yesterday should be in incomplete state",
				dataLineItems[dataLineItems.lastIndex - 1 - FUTURE_DATES_NUMBER].tasks[0].completed)
		assertFalse(
				"second task for day before yesterday should be in failed state",
				dataLineItems[dataLineItems.lastIndex - 2 - FUTURE_DATES_NUMBER].tasks[1].completed!!)
		assertNull(
				"first task for day before yesterday should be in incomplete state",
				dataLineItems[dataLineItems.lastIndex - 2 - FUTURE_DATES_NUMBER].tasks[0].completed)
		assertNull(
				"second task for 2 days before yesterday should be in incomplete state",
				dataLineItems[dataLineItems.lastIndex - 3 - FUTURE_DATES_NUMBER].tasks[1].completed)
		assertNull(
				"first task for 2 days before yesterday should be in incomplete state",
				dataLineItems[dataLineItems.lastIndex - 3 - FUTURE_DATES_NUMBER].tasks[0].completed)
	}

	@Test
	fun shouldReturnReturnBadRequestIfTimeOffsetWasNotProvidedByClient() {
		val testUser: UserForTests = createUser()
		mockMvc.perform(post("/rest/task/daily")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.header("AX-GTD-User-ID", testUser.userId)
				.with(SecurityMockMvcRequestPostProcessors.user(testUser.username).password(testUser.password))
				.content(parser.toJson(NewTaskDTO("Some_taskName"))))
				.andExpect(status().isBadRequest)

		mockMvc.perform(post("/rest/task/daily")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.header("AX-GTD-User-ID", testUser.userId)
				.header(TIME_OFFSET_HEADER, 180)
				.with(SecurityMockMvcRequestPostProcessors.user(testUser.username).password(testUser.password))
				.content(parser.toJson(NewTaskDTO("Some_taskName"))))
				.andExpect(status().isOk)
	}

	/**
	 * Установить дату на 05.02.2014 22:00
	 * Создать таск для нового пользователя (смещение 0 часов)
	 * Установить дату на 06.02.2014 22:00
	 * Запросить информацию о таске и ожидать (смещение 0 часов)
 	 *									 	05.02.2014 -> false
 	 * 										06.02.2014 -> null
	 * Успешно завершить задачу на сегодня (смещение 0 часов)
	 * Запросить информацию о таске и ожидать (смещение 0 часов)
	 * 										05.02.2014 -> false
	 * 										06.02.2014 -> true
	 * Запросить информацию о таске и ожидать (смещение -3 часа)
	 * 										05.02.2014 -> false
	 * 										06.02.2014 -> true
	 * 										07.02.2014 -> null
	 */
	@Test
	fun shouldCompleteTodayTask() {
		val testUser: UserForTests = createUser()
		val offset = 0
		taskService.dateService.setTimeInstant(utcInstant(2014, 2, 5, 22))
		var taskId: Long = -1
		mockMvc.perform(post("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, offset)
				.header("AX-GTD-User-ID", testUser.userId)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(parser.toJson(NewTaskDTO("Completable_user_task"))))
				.andExpect(status().isOk)
				.andDo { taskId = jsonParser.parse(it.response.contentAsString).asJsonObject["id"].asLong }

		taskService.dateService.setTimeInstant(utcInstant(2014, 2, 6, 22))
		val firstDayItems: List<DatelineItemDTO> = retrieveDateLineItems(testUser, offset)
		val firstStates = listOf(false, null).plus(nullList(FUTURE_DATES_NUMBER))
		assertThat("should be failed for yesterday incomplete for today", firstDayItems, matches(firstStates))

		mockMvc.perform(put("/rest/task/daily/$taskId/state")
				.header(TIME_OFFSET_HEADER, offset)
				.header("AX-GTD-User-ID", testUser.userId)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(parser.toJson(mapOf("value" to "done"))))
				.andExpect(status().isOk)

		val firstCompletedItems: List<DatelineItemDTO> = retrieveDateLineItems(testUser, offset)
		val afterCompleteStates = listOf<Boolean?>(false, true).plus(nullList(FUTURE_DATES_NUMBER))
		assertThat("task should be complete for today", firstCompletedItems, matches(afterCompleteStates))

		val secondCompletedItems: List<DatelineItemDTO> = retrieveDateLineItems(testUser, -180)
		val afterOffsetStates = listOf(false, true, null).plus(nullList(FUTURE_DATES_NUMBER))
		assertThat("task should be complete for yesterday", secondCompletedItems, matches(afterOffsetStates))
	}

	@Test
	fun shouldEditTaskName() {
		// Create new user
		val user = createUser()

		// Create new task for the user
		val NAME_BEFORE_EDIT = "nameBeforeEdit"
		val NAME_AFTER_EDIT = "nameAfterEdit"

		var taskId: Long = -1
		mockMvc.perform(post("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, 0)
				.header("AX-GTD-User-ID", user.userId)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.with(SecurityMockMvcRequestPostProcessors.user(user.username).password(user.password))
				.content(parser.toJson(NewTaskDTO(NAME_BEFORE_EDIT))))
				.andExpect(status().isOk)
				.andDo { taskId = jsonParser.parse(it.response.contentAsString).asJsonObject["id"].asLong }

		mockMvc.perform(get("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, 0)
				.header("AX-GTD-User-ID", user.userId)
				.with(SecurityMockMvcRequestPostProcessors.user(user.username).password(user.password)))
				.andExpect(status().isOk)
				.andExpect(jsonPath("tasks[0].name", Matchers.`is`(NAME_BEFORE_EDIT)))

		// Edit created task name and check
		mockMvc.perform(put("/rest/task/daily/$taskId/name")
				.header(TIME_OFFSET_HEADER, 0)
				.header("AX-GTD-User-ID", user.userId)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.with(SecurityMockMvcRequestPostProcessors.user(user.username).password(user.password))
				.content(parser.toJson(mapOf("name" to NAME_AFTER_EDIT))))
				.andExpect(status().isOk)

		mockMvc.perform(get("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, 0)
				.header("AX-GTD-User-ID", user.userId)
				.with(SecurityMockMvcRequestPostProcessors.user(user.username).password(user.password)))
				.andExpect(status().isOk)
				.andExpect(jsonPath("tasks[0].name", Matchers.`is`(NAME_AFTER_EDIT)))

	}

	@Test
	fun shouldChangeDailyTaskItemState() {
		// Create new user
		val user = createUser()

		// Create new task for the user
		val FAILING_TASK_NAME = "nameBeforeEdit"

		var taskId: Long = -1
		mockMvc.perform(post("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, 0)
				.header("AX-GTD-User-ID", user.userId)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.with(SecurityMockMvcRequestPostProcessors.user(user.username).password(user.password))
				.content(parser.toJson(NewTaskDTO(FAILING_TASK_NAME))))
				.andExpect(status().isOk)
				.andDo { taskId = jsonParser.parse(it.response.contentAsString).asJsonObject["id"].asLong }

		val dateLineItemsBefore = retrieveDateLineItems(user, 0)
		val todayStateBefore = dateLineItemsBefore.find { it.today }!!
		assertNull("today state for just created task should be null", todayStateBefore.tasks.first().completed)

		// change today task state to failed
		mockMvc.perform(put("/rest/task/daily/$taskId/state")
				.header(TIME_OFFSET_HEADER, 0)
				.header("AX-GTD-User-ID", user.userId)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.with(SecurityMockMvcRequestPostProcessors.user(user.username).password(user.password))
				.content(parser.toJson(mapOf("value" to "failed"))))
				.andExpect(status().isOk)

		val dateLineItemsAfter = retrieveDateLineItems(user, 0)
		val todayStateAfter = dateLineItemsAfter.find { it.today }!!
		assertFalse("today state should be failed", todayStateAfter.tasks.first().completed!!)
	}

	@Test
	fun shouldCreatePomodoroTypeTask() {
		val user = createUser()

		val POMODORO_TASK_NAME = "tomato_task"
		val INSTANT_TASK_NAME = "instant_task"

		// create two types of tasks
		val newPomodoroTask = NewTaskDTO(name = POMODORO_TASK_NAME, type = "pomodoro")
		mockMvc.perform(post("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, 0)
				.header("AX-GTD-User-ID", user.userId)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.with(SecurityMockMvcRequestPostProcessors.user(user.username).password(user.password))
				.content(parser.toJson(newPomodoroTask)))
				.andExpect(status().isOk)
		val newInstantTask = NewTaskDTO(name = INSTANT_TASK_NAME, type = "instant")
		mockMvc.perform(post("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, 0)
				.header("AX-GTD-User-ID", user.userId)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.with(SecurityMockMvcRequestPostProcessors.user(user.username).password(user.password))
				.content(parser.toJson(newInstantTask)))
				.andExpect(status().isOk)

		// retrieve and check tasks
		val dailyInfo = retrieveDailyInfo(user, 0)
		val pomodoroTask = dailyInfo.tasks.find { it.name == POMODORO_TASK_NAME }!!
		val instantTask = dailyInfo.tasks.find { it.name == INSTANT_TASK_NAME }!!
		assertEquals("Pomodoro task should have 'pomodoro' type", "POMODORO", pomodoroTask.type)
		assertEquals("Instant task should have 'instant' type", "INSTANT", instantTask.type)
	}

	@Test
	fun shouldNotBeAbleToChangeOtherUsersState() {
		val INSTANT_TASK_NAME = "inst_task_name"

		// create two users and task for first user
		val firstUser = createUser()
		val secondUser = createUser()
		val newInstantTask = NewTaskDTO(name = INSTANT_TASK_NAME, type = "instant")
		mockMvc.perform(post("/rest/task/daily")
								.header(TIME_OFFSET_HEADER, 0)
								.header("AX-GTD-User-ID", firstUser.userId)
								.contentType(MediaType.APPLICATION_JSON_UTF8)
								.content(parser.toJson(newInstantTask)))
				.andExpect(status().isOk)
		val dateLineItemsBefore = retrieveDateLineItems(firstUser, 0)
		val taskId = dateLineItemsBefore.first().tasks.first().id

		// change task state using second user credentials
		mockMvc.perform(put("/rest/task/daily/$taskId/state")
								.header(TIME_OFFSET_HEADER, 0)
								.contentType(MediaType.APPLICATION_JSON_UTF8)
								.header("AX-GTD-User-ID", secondUser.userId)
								.content(parser.toJson(mapOf("value" to "failed"))))
				.andExpect(status().isForbidden)


		// check task state was not modified and sever response is 'no allowed'
		val dateLineItemsAfter = retrieveDateLineItems(firstUser, 0)
		val isCompleted: Boolean? = dateLineItemsAfter.find { it.today }!!.tasks.first().completed

		assertNull("Task state should not change", isCompleted)
	}

	@Test
	fun shouldDeleteSpecificTask() {
		val user = createUser()
		val firstTask = NewTaskDTO(name = "first_task_name", type = "instant")
		val firstTaskResult = mockMvc.perform(post("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, 0)
				.header("AX-GTD-User-ID", user.userId)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(parser.toJson(firstTask)))
				.andExpect(status().isOk)
				.andReturn()
		val firstTaskId = jsonParser.parse(firstTaskResult.response.contentAsString).asJsonObject["id"].asLong

		val secondTask = NewTaskDTO(name = "first_task_name", type = "pomodoro")
		val secondTaskResult = mockMvc.perform(post("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, 0)
				.header("AX-GTD-User-ID", user.userId)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(parser.toJson(secondTask)))
				.andExpect(status().isOk)
				.andReturn()
		val secondTaskId = jsonParser.parse(secondTaskResult.response.contentAsString).asJsonObject["id"].asLong

		val dateLineItemsBeforeDelete = retrieveDateLineItems(user, 0)
		Assert.assertEquals("should have 2 tasks",2, dateLineItemsBeforeDelete.first().tasks.size)
		Assert.assertTrue(dateLineItemsBeforeDelete.first().tasks.any { it.id == firstTaskId })
		Assert.assertTrue(dateLineItemsBeforeDelete.first().tasks.any { it.id == secondTaskId })

		mockMvc.perform(delete("/rest/task/daily/$firstTaskId")
				.header(TIME_OFFSET_HEADER, 0)
				.header("AX-GTD-User-ID", user.userId)
		).andExpect(status().isOk)

		val dateLineItemsAfterDelete = retrieveDateLineItems(user, 0)
		Assert.assertEquals("should have only 1 tasks",1, dateLineItemsAfterDelete.first().tasks.size)
		Assert.assertFalse(dateLineItemsAfterDelete.first().tasks.any { it.id == firstTaskId })
		Assert.assertTrue(dateLineItemsAfterDelete.first().tasks.any { it.id == secondTaskId })
	}

	private fun retrieveDateLineItems(testUser: UserForTests, minutesOffset: Int): List<DatelineItemDTO> {
		val dailyData: DailyDTO = retrieveDailyInfo(testUser, minutesOffset)
		return dailyData.dateLineItems
	}

	private fun retrieveDailyInfo(testUser: UserForTests, minutesOffset: Int): DailyDTO {
		val mvcResult = mockMvc.perform(get("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, minutesOffset)
				.header("AX-GTD-User-ID", testUser.userId)
				.with(SecurityMockMvcRequestPostProcessors.user(testUser.username).password(testUser.password)))
				.andExpect(status().isOk)
				.andReturn()
		val dailyData: DailyDTO = parser.fromJson(mvcResult.response.contentAsString)
		return dailyData
	}

	internal class DateLineStateMatcher
	private constructor(val states: List<Boolean?>): TypeSafeMatcher<List<DatelineItemDTO>>() {

		companion object {
			fun matches(list: List<Boolean?>): DateLineStateMatcher {
				return DateLineStateMatcher(list)
			}
		}

		override fun describeTo(description: Description) {
			description.appendText("matches daily date items task state")
		}

		override fun matchesSafely(list: List<DatelineItemDTO>): Boolean {
			val lastElements = list.subList(list.lastIndex - states.size + 1, list.lastIndex + 1)
			val firstTaskStates = lastElements.reversed().map { it.tasks.first().completed }

			val reversedStates = states.reversed()
			return reversedStates.indices.all { reversedStates[it] == firstTaskStates[it] }
		}
	}
}