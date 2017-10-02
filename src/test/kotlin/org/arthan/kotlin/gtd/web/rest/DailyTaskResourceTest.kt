package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.domain.service.DateService
import org.arthan.kotlin.gtd.domain.service.TaskService
import org.arthan.kotlin.gtd.web.rest.DailyTaskResourceTest.DateLineStateMatcher.Companion.matches
import org.arthan.kotlin.gtd.web.rest.dto.*
import org.hamcrest.Description
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
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
		val name = "test_task"
        val taskDTO = NewTaskDTO(name)
        val postRequest = post("/rest/task/daily")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
				.header(TIME_OFFSET_HEADER, 180)
				.content(parser.toJson(taskDTO))

        val getRequest = get("/rest/task/daily")
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
						.header(TIME_OFFSET_HEADER, 180)
						.with(SecurityMockMvcRequestPostProcessors.user(testUser.username).password(testUser.password))
						.content(parser.toJson(NewTaskDTO(firstTaskName))))
				.andExpect(status().isOk)

		// set date to 2 days before
		doReturn(nowDate.minusDays(2)).`when`(spyDateService).getDay(Mockito.anyInt())
		val secondMvcResult = mockMvc.perform(
				post("/rest/task/daily")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.header(TIME_OFFSET_HEADER, 180)
						.with(SecurityMockMvcRequestPostProcessors.user(testUser.username).password(testUser.password))
						.content(parser.toJson(NewTaskDTO(secondTaskName))))
				.andExpect(status().isOk)
				.andReturn()
		val secondTaskId = jsonParser.parse(secondMvcResult.response.contentAsString).asJsonObject["id"].asLong

		taskService.dateService = DateService()
		val dataLineItems: List<DatelineItemDTO> = retrieveDateLineItems(testUser, 180)

		assertEquals("date items should have two tasks", 2, dataLineItems.first().tasks.size)
		assertEquals("tasks should be in correct order", secondTaskId, dataLineItems.first().tasks[0].id)
		assertTrue(
				"tasks for today should be in incomplete state",
				dataLineItems.last().tasks.all { it.completed == null })
		assertFalse(
				"second task for yesterday should be in failed state",
				dataLineItems[dataLineItems.lastIndex - 1 - FUTURE_DATES_NUMBER].tasks[0].completed!!)
		assertNull(
				"first task for yesterday should be in incomplete state",
				dataLineItems[dataLineItems.lastIndex - 1 - FUTURE_DATES_NUMBER].tasks[1].completed)
		assertFalse(
				"second task for day before yesterday should be in failed state",
				dataLineItems[dataLineItems.lastIndex - 2 - FUTURE_DATES_NUMBER].tasks[0].completed!!)
		assertNull(
				"first task for day before yesterday should be in incomplete state",
				dataLineItems[dataLineItems.lastIndex - 2 - FUTURE_DATES_NUMBER].tasks[1].completed)
		assertNull(
				"second task for 2 days before yesterday should be in incomplete state",
				dataLineItems[dataLineItems.lastIndex - 3 - FUTURE_DATES_NUMBER].tasks[0].completed)
		assertNull(
				"first task for 2 days before yesterday should be in incomplete state",
				dataLineItems[dataLineItems.lastIndex - 3 - FUTURE_DATES_NUMBER].tasks[1].completed)
	}

	@Test
	fun shouldReturnReturnBadRequestIfTimeOffsetWasNotProvidedByClient() {
		val testUser: UserForTests = createUser()
		mockMvc.perform(post("/rest/task/daily")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.with(SecurityMockMvcRequestPostProcessors.user(testUser.username).password(testUser.password))
				.content(parser.toJson(NewTaskDTO("Some_taskName"))))
				.andExpect(status().isBadRequest)

		mockMvc.perform(post("/rest/task/daily")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
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
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.with(SecurityMockMvcRequestPostProcessors.user(testUser.username).password(testUser.password))
				.content(parser.toJson(NewTaskDTO("Completable_user_task"))))
				.andExpect(status().isOk)
				.andDo { taskId = jsonParser.parse(it.response.contentAsString).asJsonObject["id"].asLong }

		taskService.dateService.setTimeInstant(utcInstant(2014, 2, 6, 22))
		val firstDayItems: List<DatelineItemDTO> = retrieveDateLineItems(testUser, offset)
		val firstStates = listOf(false, null).plus(nullList(FUTURE_DATES_NUMBER))
		assertThat("should be failed for yesterday incomplete for today", firstDayItems, matches(firstStates))

		mockMvc.perform(post("/rest/task/daily/$taskId/complete")
				.header(TIME_OFFSET_HEADER, offset)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.with(SecurityMockMvcRequestPostProcessors.user(testUser.username).password(testUser.password)))
				.andExpect(status().isOk)

		val firstCompletedItems: List<DatelineItemDTO> = retrieveDateLineItems(testUser, offset)
		val afterCompleteStates = listOf<Boolean?>(false, true).plus(nullList(FUTURE_DATES_NUMBER))
		assertThat("task should be complete for today", firstCompletedItems, matches(afterCompleteStates))

		val secondCompletedItems: List<DatelineItemDTO> = retrieveDateLineItems(testUser, -180)
		val afterOffsetStates = listOf(false, true, null).plus(nullList(FUTURE_DATES_NUMBER))
		assertThat("task should be complete for yesterday", secondCompletedItems, matches(afterOffsetStates))
	}

	private fun nullList(number: Int): List<Boolean?> {
		val resultList = mutableListOf<Boolean?>()
		for (i in 1..number) {
			resultList.add(null)
		}
		return resultList
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
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.with(SecurityMockMvcRequestPostProcessors.user(user.username).password(user.password))
				.content(parser.toJson(NewTaskDTO(NAME_BEFORE_EDIT))))
				.andExpect(status().isOk)
				.andDo { taskId = jsonParser.parse(it.response.contentAsString).asJsonObject["id"].asLong }

		mockMvc.perform(get("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, 0)
				.with(SecurityMockMvcRequestPostProcessors.user(user.username).password(user.password)))
				.andExpect(status().isOk)
				.andExpect(jsonPath("tasks[0].name", Matchers.`is`(NAME_BEFORE_EDIT)))

		// Edit created task name and check
		mockMvc.perform(put("/rest/task/daily/$taskId/name")
				.header(TIME_OFFSET_HEADER, 0)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.with(SecurityMockMvcRequestPostProcessors.user(user.username).password(user.password))
				.content(parser.toJson(mapOf("name" to NAME_AFTER_EDIT))))
				.andExpect(status().isOk)

		mockMvc.perform(get("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, 0)
				.with(SecurityMockMvcRequestPostProcessors.user(user.username).password(user.password)))
				.andExpect(status().isOk)
				.andExpect(jsonPath("tasks[0].name", Matchers.`is`(NAME_AFTER_EDIT)))

	}

	private fun retrieveDateLineItems(testUser: UserForTests, minutesOffset: Int): List<DatelineItemDTO> {
		val mvcResult = mockMvc.perform(get("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, minutesOffset)
				.with(SecurityMockMvcRequestPostProcessors.user(testUser.username).password(testUser.password)))
				.andExpect(status().isOk)
				.andReturn()
		val dailyData: DailyDTO = parser.fromJson(mvcResult.response.contentAsString)
		return dailyData.dateLineItems
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