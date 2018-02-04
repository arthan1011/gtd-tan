package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


/**
 * Created by arthan on 19.08.2017. | Project gtd-tan
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="administrator",roles= arrayOf("USER","ADMIN"))
@ActiveProfiles(profiles = arrayOf("test"))
class UserControllerWithDataTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun shouldGetUser() {
        val username = "test_username"
        val password = "test_password"
        val role = "role_admin"
        val isEnabled = false

        val userId = userRepository.save(User(username, password, role, isEnabled)).id

        mockMvc.perform(get("/user/$userId"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("username", `is`(username)))
                .andExpect(jsonPath("password", `is`(password)))
                .andExpect(jsonPath("role", `is`(role)))
                .andExpect(jsonPath("enabled", `is`(isEnabled)))
    }
}