package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.`will return`
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.extractEntity
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.toMono

@UnitTest
@DisplayName("UtilsExtensionsTest Unit Tests")
private class UtilsExtensionsTest : BasicIntegrationTest() {

    private companion object {
        const val BAR = "BAR"
        const val FOO = "FOO"
    }

    private data class Foo(val bar: String = BAR)
    private data class Bar(val foo: String = FOO)


    @Test
    fun withBodyTest() {
        val serverResponseMono = ok() withBody Foo()

        serverResponseMono.subscribe {
            it.headers().contentType `should equal` APPLICATION_JSON_UTF8

            val foo: Foo = it.extractEntity()
            foo.bar `should equal to` BAR
        }
    }

    @Test
    fun toBodyTest() {
        val serverResponseMono = ok().toBody(Foo())

        serverResponseMono.subscribe {
            val foo: Foo = it.extractEntity()
            foo.bar `should equal to` BAR
        }
    }

    @Test
    fun extractTest() {
        val serverRequest = mock<ServerRequest>()
        (serverRequest `will return` Foo().toMono()).bodyToMono(any<Class<Any>>())

        val result = serverRequest.extract<Foo>().block()
        result `should be instance of` Foo::class
        result.bar `should equal to` BAR
    }

    @Test
    fun extractErrorTest() {
        val serverRequest = mock<ServerRequest>()
        doReturn(Bar().toMono()).whenever(serverRequest).bodyToMono(any<Class<Any>>())

        val extract = { serverRequest.extract<Foo>().block() }
        extract `should throw` ClassCastException::class
    }

    @Test
    fun getLoggerTest() {
        val logger = getLogger<UtilsExtensionsTest>()
        logger `should not be` null
        logger `should be instance of` org.slf4j.Logger::class
        logger.name `should equal to` UtilsExtensionsTest::class.qualifiedName!!
    }
}
