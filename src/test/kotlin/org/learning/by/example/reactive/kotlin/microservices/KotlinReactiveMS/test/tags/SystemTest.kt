package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags

import org.junit.Before
import org.junit.runner.RunWith
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.application.ApplicationConfig
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.application.KotlinReactiveMsApplication
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = arrayOf(ApplicationConfig::class, KotlinReactiveMsApplication::class))
abstract class SystemTest : BasicIntegrationTest(){

    @LocalServerPort
    var serverPort : Int = 0

    @Before
    fun setup(){
        bindToPort(serverPort)
    }
}