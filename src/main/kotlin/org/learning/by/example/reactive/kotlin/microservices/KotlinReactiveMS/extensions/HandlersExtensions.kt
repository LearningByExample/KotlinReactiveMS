package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse.BodyBuilder
import reactor.core.publisher.Mono

fun <T : Any> BodyBuilder.addObject(value: T) = this.body(Mono.just(value), value.javaClass)!!
fun BodyBuilder.setJsonContent() = this.contentType(MediaType.APPLICATION_JSON_UTF8)!!
fun <T : Any> BodyBuilder.json(value: T) = this.setJsonContent().addObject(value)
