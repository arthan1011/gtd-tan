package org.arthan.kotlin.gtd

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.arthan.kotlin.gtd.web.ForwardFilter
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean

@EnableZuulProxy
@SpringBootApplication
class GtdTanApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(GtdTanApplication::class.java, *args)

        }
    }

    @Bean
    fun jacksonKotlinModule(): Module = KotlinModule()

    @Bean
    fun filterRegistrationBean(): FilterRegistrationBean {
        val registration: FilterRegistrationBean = FilterRegistrationBean()
        registration.filter = ForwardFilter()
        registration.isEnabled = true
        registration.addUrlPatterns("/")
        registration.setName("forwardFilter")
        return registration
    }
}
