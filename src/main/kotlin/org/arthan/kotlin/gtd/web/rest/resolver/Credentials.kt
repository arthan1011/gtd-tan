package org.arthan.kotlin.gtd.web.rest.resolver

/**
 * Created by arthan on 21.11.2017. | Project gtd-tan
 */
data class Credentials(val userId: Long) {
    constructor() : this(-1)
}