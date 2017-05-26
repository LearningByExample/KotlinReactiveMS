package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.extractEntity
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.web.reactive.function.server.ServerResponse.ok

class HandlersExtensionsTests : UnitTest(){

    private data class SimpleObject (val text : String, val number : Int)

    private companion object {
        const val TEXT = "text"
        const val NUMBER = 100
        val SIMPLE_OBJECT = SimpleObject(TEXT, NUMBER)
    }

    @Test
    fun withTest() {
        val serverResponseMono = ok() with SIMPLE_OBJECT

        serverResponseMono.subscribe {
            assert.that(it.headers().contentType, equalTo(APPLICATION_JSON_UTF8))

            val simpleObject : SimpleObject = it.extractEntity()
            assert.that(simpleObject.text, equalTo(TEXT))
            assert.that(simpleObject.number, equalTo(NUMBER))
        }
    }
}
