package org.arthan.kotlin.gtd

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
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
}
