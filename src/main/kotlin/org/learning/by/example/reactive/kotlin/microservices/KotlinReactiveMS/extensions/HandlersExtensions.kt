package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions

import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.web.reactive.function.server.ServerResponse.BodyBuilder
import reactor.core.publisher.Mono

internal infix fun <T : Any> BodyBuilder.with(value: T) = this content APPLICATION_JSON_UTF8 and value
private infix fun BodyBuilder.content(mediaType: MediaType) = this.contentType(mediaType)!!
private infix fun <T : Any> BodyBuilder.and(value: T) = this.body(Mono.just(value), value.javaClass)!!

