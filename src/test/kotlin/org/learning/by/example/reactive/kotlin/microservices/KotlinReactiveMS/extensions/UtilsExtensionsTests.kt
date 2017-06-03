package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isA
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.extractEntity
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.isNull
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.toMono

@UnitTest
@DisplayName("UtilsExtensionsTests Unit Tests")
private class UtilsExtensionsTests : BasicIntegrationTest() {

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
            assert.that(it.headers().contentType, equalTo(APPLICATION_JSON_UTF8))

            val foo: Foo = it.extractEntity()
            assert.that(foo.bar, equalTo(BAR))
        }
    }

    @Test
    fun toBodyTest() {
        val serverResponseMono = ok().toBody(Foo())

        serverResponseMono.subscribe {
            val foo: Foo = it.extractEntity()
            assert.that(foo.bar, equalTo(BAR))
        }
    }

    @Test
    fun extractTest() {
        val serverRequest = mock<ServerRequest>()
        doReturn(Foo().toMono()).whenever(serverRequest).bodyToMono(any<Class<Any>>())

        val result = serverRequest.extract<Foo>().block()

        assert.that(result, isA<Foo>())
        assert.that(result.bar, equalTo(BAR))
    }

    @Test
    fun extractErrorTest() {
        val serverRequest = mock<ServerRequest>()
        doReturn(Bar().toMono()).whenever(serverRequest).bodyToMono(any<Class<Any>>())

        try {
            val result = serverRequest.extract<Foo>().block()
            assert.that(result, isNull())
        } catch (throwable: Throwable) {
            assert.that(throwable, isA<ClassCastException>())
        }
    }

    @Test
    fun getLoggerTest(){
        val logger = getLogger<UtilsExtensionsTests>()
        assert.that(logger, !isNull())
        assert.that(logger, isA<org.slf4j.Logger>())
        assert.that(logger.name, equalTo(UtilsExtensionsTests::class.qualifiedName))
    }
}
