package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.natpryce.hamkrest.Matcher
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.toMono
import org.springframework.web.reactive.function.server.EntityResponse
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> ServerResponse.extractEntity() =
        (this as EntityResponse<Mono<T>>).entity().block()!!

fun getResourceAsText(resource: String) = Unit.javaClass.getResource(resource).readText()

fun <T : Any> String.getObjectFromJson(type: KClass<T>) = ObjectMapper().readValue(this, type.java)

fun <T : Any> getMonoFromJsonPath(jsonPath: String, type: KClass<T>) =
        getResourceAsText(jsonPath).getObjectFromJson(type).toMono()

fun Any?.isNullValue(): Boolean = this == null
fun isNull() = Matcher(Any?::isNullValue)