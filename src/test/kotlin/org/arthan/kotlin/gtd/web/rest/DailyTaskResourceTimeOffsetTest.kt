package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.domain.service.TaskService
import org.arthan.kotlin.gtd.web.rest.dto.*
import org.arthan.kotlin.gtd.web.rest.resolver.TIME_OFFSET_HEADER
import org.junit.After
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
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by arthan on 20.08.2017. | Project gtd-tan
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="administrator",roles= arrayOf("USER","ADMIN"))
@ActiveProfiles(profiles = arrayOf("test"))
class DailyTaskResourceTimeOffsetTest {

	companion object {
		private val USERNAME_1: String = randomName()
		private val PASSWORD_1: String = randomName()
		private var USER_ID_1: Long = -1
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

	@After
	fun tearDown() {
		taskService.dateService.setTimeInstant(Instant.now())
	}

	private fun initUsers() {
		USER_ID_1 = userRepo.save(User(USERNAME_1, PASSWORD_1, "USER", true)).id
    }

	@Test
	fun shouldChangeTodayDateDependingOnClientTimeOffset() {
		val year = 2016
		val month = 2
		val day = 1
		taskService.dateService.setTimeInstant(utcInstant(year, month, day, 2))
		mockMvc.perform(post("/rest/task/daily")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.header("AX-GTD-User-ID", USER_ID_1)
				.header(TIME_OFFSET_HEADER, 0)
				.with(SecurityMockMvcRequestPostProcessors.user(USERNAME_1).password(PASSWORD_1))
				.content(parser.toJson(NewTaskDTO(randomName()))))
				.andExpect(status().isOk)
				.andReturn()

		val noOffsetDateItems = retrieveDateLineItems()
		val firstDate = noOffsetDateItems.find { it.today }!!.date
		assertEquals("Should be the same year", year, firstDate.year)
		assertEquals("Should be the same month", month, firstDate.month)
		assertEquals("Should be the same day", day, firstDate.day)
		assertNull("task for today should be in incomplete state", noOffsetDateItems.last().tasks.first().completed)

		val offsetDateItems = retrieveDateLineItems(180)
		val secondDate = offsetDateItems.find { it.today }!!.date
		assertEquals("Should be the same year", year, secondDate.year)
		assertEquals("Should be the last month", month - 1, secondDate.month)
		assertEquals("Should be the last day", 31, secondDate.day)
		assertNull("task for today should be in incomplete state", offsetDateItems.last().tasks.first().completed)
	}

	private fun retrieveDateLineItems(offsetMinutes: Int = 0): List<DatelineItemDTO> {
		val mvcResult =
				mockMvc.perform(get("/rest/task/daily")
						.header(TIME_OFFSET_HEADER, offsetMinutes)
						.header("AX-GTD-User-ID", USER_ID_1)
						.with(SecurityMockMvcRequestPostProcessors.user(USERNAME_1).password(PASSWORD_1)))
						.andExpect(status().isOk)
						.andReturn()
		val dailyData: DailyDTO = parser.fromJson(mvcResult.response.contentAsString)
		return dailyData.dateLineItems
	}
}