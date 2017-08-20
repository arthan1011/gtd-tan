package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.web.rest.dto.DailyDTO
import org.arthan.kotlin.gtd.web.rest.dto.DailyTaskDTO
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
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

    val initialized: AtomicBoolean = AtomicBoolean(false)
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var userRepo: UserRepository

    @Before
    fun setUp() {
        if (!initialized.get()) {
            initUser()
            initialized.set(true)
        }
    }

    private fun initUser() {
        userRepo.save(User("administrator", "password", "ADMIN", enabled = true))
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
}