package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.application

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Before
import org.junit.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.HelloResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.SystemTest

class KotlinReactiveMsApplicationTests : SystemTest() {

    @Before
    fun setup() = bindToServer()

    @Test
    fun apiGet(){
        val helloResponse = get( url = "/api/hello", type = HelloResponse::class)
        assert.that(helloResponse.hello, equalTo("world"))
    }
}