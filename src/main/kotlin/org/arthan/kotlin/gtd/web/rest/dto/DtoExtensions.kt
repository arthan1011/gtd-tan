package org.arthan.kotlin.gtd.web.rest.dto

import org.arthan.kotlin.gtd.domain.model.DailyTask

/**
 * Extension functions for model objects
 *
 * Created by arthan on 23.07.2017. | Project gtd-tan
 */

fun DailyTask.toTO(): DailyTaskTO {
    return DailyTaskTO(name ?: "")
}
