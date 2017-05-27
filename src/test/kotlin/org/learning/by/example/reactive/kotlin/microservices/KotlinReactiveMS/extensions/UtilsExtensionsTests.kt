package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isA
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.extractEntity
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

@UnitTest
@DisplayName("UtilsExtensionsTests Unit Tests")
private class UtilsExtensionsTests : BasicIntegrationTest() {

    private data class SimpleObject(val text: String, val number: Int)

    private companion object {
        const val TEXT = "text"
        const val NUMBER = 100
        val SIMPLE_OBJECT = SimpleObject(TEXT, NUMBER)
    }

    @Test
    fun toMonoTest() {

        SIMPLE_OBJECT.toMono().subscribe({
            assert.that(it.text, equalTo(TEXT))
            assert.that(it.number, equalTo(NUMBER))
        })
    }

    @Test
    fun toMonoThrowableTest() {

        val message : Mono<String> = RuntimeException(TEXT).toMono()

        message.onErrorResume({
            assert.that(it, isA<RuntimeException>())
            assert.that(it.message, equalTo(TEXT))
            Mono.empty()
        }).block()
    }

    @Test
    fun withBodyTest() {
        val serverResponseMono = ok() withBody SIMPLE_OBJECT

        serverResponseMono.subscribe({
            assert.that(it.headers().contentType, equalTo(APPLICATION_JSON_UTF8))

            val simpleObject: SimpleObject = it.extractEntity()
            assert.that(simpleObject.text, equalTo(TEXT))
            assert.that(simpleObject.number, equalTo(NUMBER))
        })
    }

    @Test
    fun toBodyTest() {
        val serverResponseMono = ok().toBody(SIMPLE_OBJECT)

        serverResponseMono.subscribe({
            val simpleObject: SimpleObject = it.extractEntity()
            assert.that(simpleObject.text, equalTo(TEXT))
            assert.that(simpleObject.number, equalTo(NUMBER))
        })
    }
}
