package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.withBody
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.status

internal class ErrorHandler {
    fun notFound(request: ServerRequest) = status(HttpStatus.NOT_FOUND) withBody ErrorResponse("not found")
}