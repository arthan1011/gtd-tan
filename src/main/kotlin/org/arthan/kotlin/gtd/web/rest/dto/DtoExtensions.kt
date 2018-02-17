package org.arthan.kotlin.gtd.web.rest.dto

import org.arthan.kotlin.gtd.domain.model.DailyTask
import java.time.LocalDate

/**
 * Extension functions for model objects
 *
 * Created by arthan on 23.07.2017. | Project gtd-tan
 */

fun DailyTask.toTO(): TaskDTO {
    return TaskDTO(id!!, name!!, type!!.name, intervals)
}

fun LocalDate.toDTO(): DateDTO {
    return DateDTO(
            day = dayOfMonth,
            month = monthValue,
            year = year
    )
}
