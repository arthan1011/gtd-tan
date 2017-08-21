package org.arthan.kotlin.gtd.web.rest.dto

/**
 * transport object fro dateline item
 * Created by shamsiev on 21.08.2017 for gtd-tan.
 */
data class DatelineItemDTO(val date: DateDTO, val tasks: List<DailyTaskDTO>, val today: Boolean)