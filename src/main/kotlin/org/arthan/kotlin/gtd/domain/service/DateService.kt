package org.arthan.kotlin.gtd.domain.service

import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Created by arthan on 12.08.2017. | Project gtd-tan
 */

@Service
class DateService {
    fun getCurrentDate(): LocalDate {
        return LocalDate.now()
    }
}