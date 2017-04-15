package org.arthan.kotlin.gtd.config

import org.arthan.kotlin.gtd.domain.service.CurrentUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import javax.sql.DataSource

/**
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

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(userDetailsService)
    }

    override fun configure(http: HttpSecurity?) {
        http?.authorizeRequests()
                ?.antMatchers("/registration")?.permitAll() // sign up page
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
                    ?.logoutSuccessUrl("/app")
                ?.invalidateHttpSession(true)
    }
}