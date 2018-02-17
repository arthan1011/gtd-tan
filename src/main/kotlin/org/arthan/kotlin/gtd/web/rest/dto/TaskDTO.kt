package org.arthan.kotlin.gtd.web.rest.dto

/**
 * Data transport object for tasks
 *
 * Created by shamsiev on 29.08.2017 for gtd-tan.
 */
data class TaskDTO(val id: Long, val name: String, val type: String, val intervals: Int?)