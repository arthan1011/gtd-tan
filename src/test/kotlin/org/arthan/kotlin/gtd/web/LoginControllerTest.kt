package org.arthan.kotlin.gtd.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.testng.Assert.*
import org.testng.annotations.Test

/**
 * Created by arthan on 4/14/17 .
 */
class LoginControllerTest {

    @Test
    fun testMapping() {
        val mapper = jacksonObjectMapper()

        val obj = NewUserForm("python", "test", "dds")
        val ob = NewUserForm()

        mapper.writeValue(System.out, obj)
    }
}