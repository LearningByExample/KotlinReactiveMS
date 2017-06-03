package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.*
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers.ThrowableTranslator.Translate
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import kotlin.reflect.full.primaryConstructor

@UnitTest
@DisplayName("ThrowableTranslator Unit Tests")
internal class ThrowableTranslatorTest : BasicIntegrationTest() {

    private inline fun <reified T : Throwable> createException(cause: Throwable? = null) =
            T::class.primaryConstructor?.call("", cause)!!

    private inline fun <reified T : Throwable, reified K : Throwable> createExceptionWithCause() =
            createException<T>(createException<K>())

    private fun Throwable.httpStatus() = Mono.just(this).transform(Translate::throwable)
            .map { it.httpStatus }.block()
    
    @Test
    fun translateGeoLocationNotFoundExceptionTest() {
        assert.that(createException<GeoLocationNotFoundException>().httpStatus(), equalTo(HttpStatus.NOT_FOUND))
    }

    @Test
    fun translateGetGeoLocationExceptionTest() {
        assert.that(createException<GetGeoLocationException>().httpStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR))
    }

    @Test
    fun translateGetSunriseSunsetExceptionTest() {
        assert.that(createException<GetSunriseSunsetException>().httpStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR))
    }

    @Test
    fun translateInvalidParametersExceptionTest() {
        assert.that(createException<InvalidParametersException>().httpStatus(), equalTo(HttpStatus.BAD_REQUEST))
    }

    @Test
    fun translateWithCauseTest() {
        assert.that(createExceptionWithCause<GetGeoLocationException, InvalidParametersException>().httpStatus(),
                equalTo(HttpStatus.BAD_REQUEST))
    }

    @Test
    fun translatePathNotFoundExceptionTest() {
        assert.that(createException<PathNotFoundException>().httpStatus(), equalTo(HttpStatus.NOT_FOUND))
    }
}
