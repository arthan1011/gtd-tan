package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.web.rest.dto.DailyDTO
import org.arthan.kotlin.gtd.web.rest.dto.DailyTaskDTO
import org.arthan.kotlin.gtd.web.rest.dto.DatelineItemDTO
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.util.*
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
		val USERNAME_1 = randomName()
		val PASSWORD_1 = randomName()
	}

    val initialized: AtomicBoolean = AtomicBoolean(false)
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var userRepo: UserRepository

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
		val name = "test_task"
		val taskDTO = DailyTaskDTO(name)
		mockMvc.perform(post("/rest/task/daily")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(parser.toJson(taskDTO)))
				.andExpect(status().isOk)

		var dailyData: DailyDTO? = null
		mockMvc.perform(get("/rest/task/daily")
				.with(SecurityMockMvcRequestPostProcessors.user(USERNAME_1).password(PASSWORD_1)))
				.andExpect(status().isOk)
				.andDo { dailyData = parser.fromJson(it.response.contentAsString) }

		val data = dailyData!!
		val dataLineItems: List<DatelineItemDTO> = data.dateLineItems
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
	}
}