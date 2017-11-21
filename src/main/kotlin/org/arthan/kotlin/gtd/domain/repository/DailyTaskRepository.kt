package org.arthan.kotlin.gtd.domain.repository

import org.arthan.kotlin.gtd.domain.model.DailyTask
import org.springframework.data.repository.CrudRepository

/**
 * Repository for DailyTask Entity
 *
 * Created by arthan on 23.07.2017. | Project gtd-tan
 */
interface DailyTaskRepository : CrudRepository<DailyTask, Long> {
    fun findByUsername(username: String): List<DailyTask>
    fun findByUserId(userId: Long): List<DailyTask>
}