package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags

import org.junit.runner.RunWith
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.application.KotlinReactiveMsApplication
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(KotlinReactiveMsApplication::class))
abstract class UnitTest : BasicIntegrationTest()
