package org.arthan.kotlin

import org.arthan.kotlin.gtd.GtdTanApplication
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(GtdTanApplication::class))
class GtdTanApplicationTests {

	@Test
	fun contextLoads() {
	}

}
