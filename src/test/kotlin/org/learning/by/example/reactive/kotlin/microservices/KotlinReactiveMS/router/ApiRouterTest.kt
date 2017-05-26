package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.router

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.HelloResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers.ApiRouter
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.IntegrationTest
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
@DisplayName("ApiRouter Integration Tests")
private class ApiRouterTest : BasicIntegrationTest() {

    @Autowired
    lateinit var apiRouter: ApiRouter

    @BeforeEach
    fun setup() = bindToRouterFunction(apiRouter.doRoute())

    @Test
    fun getHello() {
        val helloResponse = get(url = "/api/hello", type = HelloResponse::class)
        assert.that(helloResponse.hello, equalTo("world"))
    }
}
