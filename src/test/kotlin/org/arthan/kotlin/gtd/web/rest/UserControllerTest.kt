package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


/**
 * Created by arthan on 19.08.2017. | Project gtd-tan
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="administrator",roles= arrayOf("USER","ADMIN"))
class UserControllerTest {

	companion object {
		private val USER_NAME = "admin"
	}

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var userRepo: UserRepository

	@Test
    fun shouldGetPrincipalName() {
        mockMvc.perform(get("/user/hello").with(user(USER_NAME).password("pass")))
                .andExpect(status().isOk)
                .andExpect(jsonPath("name", `is`(USER_NAME)))
                .andDo { println(it.response.contentAsString) }
    }

    @Test
    fun shouldGetUser() {
        val username = "test_username"
        val password = "test_password"
        val role = "role_admin"
        val isEnabled = false
        Mockito.`when`(userRepo.findOne(1L)).thenReturn(User(
                username, password, role, isEnabled))
        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("username", `is`(username)))
                .andExpect(jsonPath("password", `is`(password)))
                .andExpect(jsonPath("role", `is`(role)))
                .andExpect(jsonPath("enabled", `is`(isEnabled)))
    }
}