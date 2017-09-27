package org.arthan.kotlin.gtd.config

import org.arthan.kotlin.gtd.domain.service.CurrentUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.sql.DataSource

/**
 * Security Configuration
 *
 * Created by arthan on 4/14/17 .
 */

@Suppress("SpringKotlinAutowiring")
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var datasource: DataSource

    @Autowired
    lateinit var userDetailsService: CurrentUserDetailsService

    override fun configure(auth: AuthenticationManagerBuilder) {
//        auth.userDetailsService(userDetailsService)
        auth.authenticationProvider(authProvider())
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/registration")?.permitAll() // sign up page
                ?.anyRequest()?.authenticated()
                ?.and()
                ?.formLogin()
                    ?.loginPage("/login")
                    ?.usernameParameter("username")
                    ?.failureUrl("/login?error")
                    ?.permitAll()
                ?.and()
                ?.logout()
                    ?.logoutUrl("/logout")
                    ?.logoutSuccessUrl("/ui")
                ?.invalidateHttpSession(true)
    }

    @Bean
    fun authProvider(): AuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}