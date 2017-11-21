package org.arthan.kotlin.gtd.web.rest.dto

/**
 * Created by arthan on 21.11.2017. | Project gtd-tan
 */
data class AuthDTO(
        var success: Boolean = false,
        var clientId: Long? = null,
        var message: String? = null
)