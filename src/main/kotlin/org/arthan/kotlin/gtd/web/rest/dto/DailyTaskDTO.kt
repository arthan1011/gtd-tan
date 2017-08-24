package org.arthan.kotlin.gtd.web.rest.dto

/**
 * DTO for daily tasks
 *
 * Created by arthan on 22.07.2017. | Project gtd-tan
 */

// TODO: разделить на два класса без null полей
data class DailyTaskDTO(
        val name: String? = null,
        val id: Long = -1L,
        val completed: Boolean? = null)