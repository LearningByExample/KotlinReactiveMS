package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.HelloResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.extractEntity
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest

class ApiHandlerTests : UnitTest() {

    @Autowired
    lateinit var apiHandler: ApiHandler

    @Test
    fun apiHandlerTest() {

        val serverRequest = mock<ServerRequest>()
        val serverResponseMono = apiHandler.getHello(serverRequest)

        serverResponseMono.subscribe {
            assert.that(it.statusCode(), equalTo(HttpStatus.OK))
            val helloResponse: HelloResponse = it.extractEntity()
            assert.that(helloResponse.hello, equalTo("world"))
        }
    }

}