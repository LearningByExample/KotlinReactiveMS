package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS

import org.junit.Test
import org.junit.runner.RunWith
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.application.ApplicationConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(ApplicationConfig::class))
class KotlinReactiveMsApplicationTests {

	@Test
	fun contextLoads() {
	}

}
