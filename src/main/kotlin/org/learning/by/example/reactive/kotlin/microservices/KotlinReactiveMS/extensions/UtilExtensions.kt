package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.BodyBuilder
import reactor.core.publisher.toMono

internal infix fun <T : Any> BodyBuilder.withBody(value: T) = this.contentType(APPLICATION_JSON_UTF8).toBody(value)
internal fun <T : Any> BodyBuilder.toBody(value: T) = this.body(value.toMono(), value.javaClass)!!
inline internal fun <reified T : Any> ServerRequest.extract() = this.bodyToMono(T::class.java)
inline internal fun <reified T : Any> getLogger() = LoggerFactory.getLogger(T::class.java)
