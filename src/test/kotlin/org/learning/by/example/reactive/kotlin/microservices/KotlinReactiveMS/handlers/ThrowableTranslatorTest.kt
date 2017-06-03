package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.*
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers.ThrowableTranslator.Translate
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

@UnitTest
@DisplayName("ThrowableTranslator Unit Tests")
private class ThrowableTranslatorTest : BasicIntegrationTest() {

    private inline fun <reified T : Throwable> createException(cause: Throwable? = null) =
            T::class.primaryConstructor?.call("", cause)!!

    private inline fun <reified T : Throwable, reified K : Throwable> createExceptionWithCause() =
            createException<T>(createException<K>())

    private var <T : Throwable> T.httpStatus: HttpStatus
        get() = Mono.just(this).transform(Translate::throwable).map { it.httpStatus }.block()
        set(value) {
            this.httpStatus = HttpStatus.I_AM_A_TEAPOT
        }

    private infix fun Throwable.`should translate to`(theStatus: HttpStatus) {
        this.httpStatus shouldEqual theStatus
    }

    @Suppress("unused")
    private inline infix fun <reified T : Throwable> KClass<T>.`translates to`(theStatus: HttpStatus) {
        createException<T>() `should translate to` theStatus
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    private inline infix fun <reified T : Throwable, reified K : Throwable> KClass<T>.withCause(otherClass: KClass<K>) =
            createExceptionWithCause<T, K>()

    @Test
    fun translateGeoLocationNotFoundExceptionTest() {
        GeoLocationNotFoundException::class `translates to` HttpStatus.NOT_FOUND
    }

    @Test
    fun translateGetGeoLocationExceptionTest() {
        GetGeoLocationException::class `translates to` HttpStatus.INTERNAL_SERVER_ERROR
    }

    @Test
    fun translateGetSunriseSunsetExceptionTest() {
        GetSunriseSunsetException::class `translates to` HttpStatus.INTERNAL_SERVER_ERROR
    }

    @Test
    fun translateInvalidParametersExceptionTest() {
        InvalidParametersException::class `translates to` HttpStatus.BAD_REQUEST
    }

    @Test
    fun translatePathNotFoundExceptionTest() {
        PathNotFoundException::class `translates to` HttpStatus.NOT_FOUND
    }

    @Test
    fun translateWithCauseTest() {
        GetGeoLocationException::class withCause InvalidParametersException::class `should translate to`
                HttpStatus.BAD_REQUEST
    }


}
