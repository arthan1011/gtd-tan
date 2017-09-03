package org.arthan.kotlin.gtd.domain.service

import org.junit.Assert.*
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Created by arthan on 03.09.2017. | Project gtd-tan
 */
class DateServiceTest {

	@Test
	fun shouldTestDate() {
		val dateService = DateService()
		val firstInstant = dateService.takeInstant()
		Thread.sleep(500)
		val secondInstant = dateService.takeInstant()
		assertNotEquals("time instants should be different after waiting", firstInstant, secondInstant)

		dateService.setTimeInstant(Instant.now())
		val firstFixedInstant = dateService.takeInstant()
		Thread.sleep(500)
		val secondFixedInstant = dateService.takeInstant()
		assertEquals("time instants should be equal when came from fixed clock", firstFixedInstant, secondFixedInstant)
	}
}