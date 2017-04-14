package org.arthan.kotlin.gtd

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.arthan.kotlin.gtd.web.filter.AppRedirectFilter
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@SpringBootApplication
class GtdTanApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(GtdTanApplication::class.java, *args)

        }
    }

    @Bean
    fun filterRegistrationBean(): FilterRegistrationBean {
        val registration: FilterRegistrationBean = FilterRegistrationBean()
        registration.filter = AppRedirectFilter()
        registration.isEnabled = true
        registration.addUrlPatterns("/app/*")
        registration.setName("appRedirectFilter")
        return registration
    }

    @Bean
    fun jacksonKotlinModule(): Module = KotlinModule()
}
