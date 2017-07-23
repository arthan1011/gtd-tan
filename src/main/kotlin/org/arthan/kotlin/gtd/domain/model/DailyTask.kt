package org.arthan.kotlin.gtd.domain.model

import javax.persistence.*

/**
 * Entity for user daily tasks
 *
 * Created by arthan on 23.07.2017. | Project gtd-tan
 */

@Entity
@Table(name = "daily_task")
@NamedQuery(
        name = "DailyTask.findByUsername",
        query = "select dt " +
                "from DailyTask dt, User u " +
                "where dt.userId = u.id and u.username = ?1"
)
data class DailyTask(
        @Id
        @GeneratedValue
        val id: Long? = null,

        @Column(name = "userid")
        var userId: Long? = null,

        var name: String? = null
)