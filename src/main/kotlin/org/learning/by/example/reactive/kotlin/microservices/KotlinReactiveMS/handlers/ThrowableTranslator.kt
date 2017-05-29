package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GeoLocationNotFoundException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GetGeoLocationException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.InvalidParametersException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.PathNotFoundException
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono

internal class ThrowableTranslator private constructor(throwable: Throwable) {

    val httpStatus: HttpStatus
    val message: String

    init {
        this.httpStatus = getStatus(throwable)
        this.message = throwable.message.toString()
    }

    private fun getStatus(throwable: Throwable): HttpStatus =
            when (throwable) {
                is InvalidParametersException -> HttpStatus.BAD_REQUEST
                is PathNotFoundException -> HttpStatus.NOT_FOUND
                is GeoLocationNotFoundException -> HttpStatus.NOT_FOUND
                is GetGeoLocationException ->
                    when (throwable.cause) {
                        is InvalidParametersException -> HttpStatus.BAD_REQUEST
                        else -> HttpStatus.INTERNAL_SERVER_ERROR
                    }
                else -> HttpStatus.INTERNAL_SERVER_ERROR
            }

    companion object Translate {
        fun <T : Throwable> throwable(throwable: Mono<T>): Mono<ThrowableTranslator> {
            return throwable.map(::ThrowableTranslator)
        }
    }
}
