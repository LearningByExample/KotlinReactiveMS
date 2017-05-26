package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test

import org.springframework.web.reactive.function.server.EntityResponse
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> ServerResponse.extractEntity() =
        (this as EntityResponse<Mono<T>>).entity().block()!!