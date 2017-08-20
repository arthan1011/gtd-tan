package org.arthan.kotlin.gtd.web.rest.dto

import org.arthan.kotlin.gtd.domain.model.DailyTask
import java.time.LocalDate

/**
 * Extension functions for model objects
 *
 * Created by arthan on 23.07.2017. | Project gtd-tan
 */

fun DailyTask.toTO(): DailyTaskDTO {
    return DailyTaskDTO(name ?: "", id!!)
}

fun LocalDate.toDTO(): DateDTO {
    return DateDTO(
            day = dayOfMonth.toString(),
            month = monthValue.toString(),
            year = year.toString()
    )
}
