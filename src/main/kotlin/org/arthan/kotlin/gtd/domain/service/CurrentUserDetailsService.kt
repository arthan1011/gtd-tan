package org.arthan.kotlin.gtd.domain.service

import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

/**
 * Created by arthan on 4/14/17 .
 */

@Service
class CurrentUserDetailsService : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByUsername(username!!)
        return CurrentUser(user!!)
    }
}

class CurrentUser(user: org.arthan.kotlin.gtd.domain.model.User) :
        User(
                user.username,
                user.password,
                AuthorityUtils.createAuthorityList(user.role))