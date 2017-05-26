package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.application

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.HelloResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.SystemTest
import org.springframework.boot.web.server.LocalServerPort


@SystemTest
@DisplayName("KotlinReactiveMsApplication System Tests")
private class KotlinReactiveMsApplicationTests : BasicIntegrationTest() {

    @LocalServerPort
    var port: Int = 0

    @BeforeEach
    fun setup() = bindToPort(port)

    @Test
    fun apiGet() {
        val helloResponse = get(url = "/api/hello", type = HelloResponse::class)
        assert.that(helloResponse.hello, equalTo("world"))
    }
}
