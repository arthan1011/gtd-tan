package org.arthan.kotlin.gtd

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.arthan.kotlin.gtd.web.ForwardFilter
import org.arthan.kotlin.gtd.web.rest.resolver.ClientMetaDataResolver
import org.arthan.kotlin.gtd.web.rest.resolver.CredentialsResolver
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import javax.sql.DataSource

@SpringBootApplication
class GtdTanApplication : WebMvcConfigurationSupport() {

	override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
		argumentResolvers.add(ClientMetaDataResolver())
		argumentResolvers.add(CredentialsResolver())
	}

	companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(GtdTanApplication::class.java, *args)
        }
    }

    @Bean
    @Profile("test")
    fun getTestDataSource(): DataSource {
        val pg = EmbeddedPostgres.start()
        return pg.postgresDatabase
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
