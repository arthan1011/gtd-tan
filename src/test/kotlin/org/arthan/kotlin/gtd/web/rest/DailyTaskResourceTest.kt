package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.domain.service.DateService
import org.arthan.kotlin.gtd.domain.service.TaskService
import org.arthan.kotlin.gtd.web.rest.dto.*
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
		val dataLineItems: List<DatelineItemDTO> = retrieveDateLineItems(testUser)
		assertEquals(
				"Incorrect number of datelineItems was retrieved",
				DailyTaskResource.DATE_LINE_ITEMS_SIZE,
				dataLineItems.size)

		val lastItem = dataLineItems.last()
		val date = LocalDate.now()

		assertTrue("last dateline item should be today", lastItem.today)
		assertEquals("last dateline item should be this year", date.year, lastItem.date.year)
		assertEquals("last dateline item should be this month", date.monthValue, lastItem.date.month)
		assertEquals("last dateline item should be this day", date.dayOfMonth, lastItem.date.day)

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
		val secondTaskName = "task_task_2"
		val dateService = DateService()
		val spyDateService = spy(dateService)
		taskService.dateService = spyDateService

		mockMvc.perform(
				post("/rest/task/daily")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.header(TIME_OFFSET_HEADER, 180)
						.with(SecurityMockMvcRequestPostProcessors.user(testUser.username).password(testUser.password))
						.content(parser.toJson(NewTaskDTO(secondTaskName))))
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
		val dataLineItems: List<DatelineItemDTO> = retrieveDateLineItems(testUser)

		assertEquals("date items should have two tasks", 2, dataLineItems.first().tasks.size)
		assertEquals("tasks should be in correct order", secondTaskId, dataLineItems.first().tasks[1].id)
		assertTrue(
				"tasks for today should be in incomplete state",
				dataLineItems.last().tasks.all { it.completed == null })
		assertFalse(
				"second task for yesterday should be in failed state",
				dataLineItems[dataLineItems.lastIndex-1].tasks[1].completed!!)
		assertNull(
				"first task for yesterday should be in incomplete state",
				dataLineItems[dataLineItems.lastIndex-1].tasks[0].completed)
		assertFalse(
				"second task for day before yesterday should be in failed state",
				dataLineItems[dataLineItems.lastIndex-2].tasks[1].completed!!)
		assertNull(
				"first task for day before yesterday should be in incomplete state",
				dataLineItems[dataLineItems.lastIndex-2].tasks[0].completed)
		assertNull(
				"second task for 2 days before yesterday should be in incomplete state",
				dataLineItems[dataLineItems.lastIndex-3].tasks[1].completed)
		assertNull(
				"first task for 2 days before yesterday should be in incomplete state",
				dataLineItems[dataLineItems.lastIndex-3].tasks[0].completed)
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

	@Test
	fun shouldCompleteTodayTask() {
		// Установить дату на 05.02.2014 22:00
		// Создать таск для нового пользователя (смещение 0 часов)
		// Установить дату на 06.02.2014 22:00
		// Запросить информацию о таске и ожидать (смещение 0 часов)
		/*                      05.02.2014 -> false
								06.02.2014 -> null */
		// Успешно завершить задачу на сегодня (смещение 0 часов)
		// Запросить информацию о таске и ожидать (смещение 0 часов)
		/*                      05.02.2014 -> false
								06.02.2014 -> true */
		// Запросить информацию о таске и ожидать (смещение -3 часа)
		/*                      05.02.2014 -> false
		                        06.02.2014 -> true
								07.02.2014 -> null */
	}

	// TODO: Проверить что при изменении смещении времени в запросе меняется текущая дата в ответе.

	private fun retrieveDateLineItems(testUser: UserForTests): List<DatelineItemDTO> {
		val mvcResult = mockMvc.perform(get("/rest/task/daily")
				.header(TIME_OFFSET_HEADER, 180)
				.with(SecurityMockMvcRequestPostProcessors.user(testUser.username).password(testUser.password)))
				.andExpect(status().isOk)
				.andReturn()
		val dailyData: DailyDTO = parser.fromJson(mvcResult.response.contentAsString)
		return dailyData.dateLineItems
	}
}