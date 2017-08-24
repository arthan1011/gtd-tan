package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.domain.service.DateService
import org.arthan.kotlin.gtd.domain.service.TaskService
import org.arthan.kotlin.gtd.web.rest.dto.DailyDTO
import org.arthan.kotlin.gtd.web.rest.dto.DailyTaskDTO
import org.arthan.kotlin.gtd.web.rest.dto.DatelineItemDTO
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
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
		private val USERNAME_1: String = randomName()
		private val PASSWORD_1: String = randomName()
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
		userRepo.save(User(USERNAME_1, PASSWORD_1, "USER", true))
    }

    @Test
    fun shouldCreateAndRetrieveOneTask() {
		val name = "test_task"
        val taskDTO = DailyTaskDTO(name)
        val postRequest = post("/rest/task/daily")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(parser.toJson(taskDTO))

        val getRequest = get("/rest/task/daily")

        // retrieve initial task list size
        var tasksSizeBefore = -1
        mockMvc.perform(getRequest)
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
        assertEquals("New daily task did not have expected name", name, tasksAfter.first().name)
        assertNotEquals("New task did not have correct id", -1, tasksAfter.first().id)
    }

	@Test
	fun shouldRetrieveDatelineItems() {
		// add one task
		val firstTaskName = "test_task_1"

		val nowDate = LocalDate.now()
		val firstMvcResult = mockMvc.perform(post("/rest/task/daily")
												.contentType(MediaType.APPLICATION_JSON_UTF8)
												.with(SecurityMockMvcRequestPostProcessors.user(USERNAME_1).password(
														PASSWORD_1))
												.content(parser.toJson(DailyTaskDTO(firstTaskName))))
				.andExpect(status().isOk)
				.andReturn()
		val firstTaskId = jsonParser.parse(firstMvcResult.response.contentAsString).asJsonObject["id"].asLong
		testRetrievedDateLineItemsHaveCorrectDates(firstTaskId)

		testDateLineItemsStatusShouldBeFailedForTasksWithStartDateInPast(nowDate)
	}

	private fun testRetrievedDateLineItemsHaveCorrectDates(savedTaskId: Long) {
		val dataLineItems: List<DatelineItemDTO> = retrieveDateLineItems()
		assertEquals(
				"Incorrect number of datelineItems was retrieved",
				DailyTaskResource.DATE_LINE_ITEMS_SIZE,
				dataLineItems.size)

		val lastItem = dataLineItems.last()
		val date = LocalDate.now()

		assertTrue("last dateline item should be today", lastItem.today)
		assertEquals("last dateline item should be this year", date.year.toString(), lastItem.date.year)
		assertEquals("last dateline item should be this month", date.monthValue.toString(), lastItem.date.month)
		assertEquals("last dateline item should be this day", date.dayOfMonth.toString(), lastItem.date.day)

		val allItemsHaveExactlyOneTask = dataLineItems.all { it.tasks.size == 1 }
		val allItemsHaveTaskWithSavedId = dataLineItems.all { it.tasks.first().id == savedTaskId }

		assertTrue("All items should have exactly one task", allItemsHaveExactlyOneTask)
		assertTrue("All items should have task with saved id", allItemsHaveTaskWithSavedId)
	}

	private fun testDateLineItemsStatusShouldBeFailedForTasksWithStartDateInPast(nowDate: LocalDate) {
		val secondTaskName = "task_task_2"
		val dateService = DateService()
		val spyDateService = spy(dateService)
		doReturn(nowDate.minusDays(2)).`when`(spyDateService).getLocalDate()
		taskService.dateService = spyDateService

		val secondMvcResult = mockMvc.perform(
				post("/rest/task/daily")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.with(SecurityMockMvcRequestPostProcessors.user(USERNAME_1).password(PASSWORD_1))
						.content(parser.toJson(DailyTaskDTO(secondTaskName))))
				.andExpect(status().isOk)
				.andReturn()
		val secondTaskId = jsonParser.parse(secondMvcResult.response.contentAsString).asJsonObject["id"].asLong

		taskService.dateService = DateService()
		val dataLineItems: List<DatelineItemDTO> = retrieveDateLineItems()

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

	// TODO: Проверить что возвращаетсся bad request, если при создании таска или при запросе тасков не пришло смещение по времени от клиента

	// TODO: Проверить что при изменении смещении времени в запросе меняется текущая дата в ответе.

	private fun retrieveDateLineItems(): List<DatelineItemDTO> {
		val mvcResult = mockMvc.perform(get("/rest/task/daily")
								.with(SecurityMockMvcRequestPostProcessors.user(USERNAME_1).password(PASSWORD_1)))
				.andExpect(status().isOk)
				.andReturn()
		val dailyData: DailyDTO = parser.fromJson(mvcResult.response.contentAsString)
		return dailyData.dateLineItems
	}
}