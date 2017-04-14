package org.arthan.kotlin.gtd

import org.arthan.kotlin.gtd.web.filter.AppRedirectFilter
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import javax.servlet.Filter

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
}
