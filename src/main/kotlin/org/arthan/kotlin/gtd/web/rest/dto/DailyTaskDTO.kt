package org.arthan.kotlin.gtd.web.rest.dto

/**
 * DTO for daily tasks
 *
 * Created by arthan on 22.07.2017. | Project gtd-tan
 */

data class DailyTaskDTO(val id: Long, val completed: Boolean? = null)