package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.PathNotFoundException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.toMono
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.withBody
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers.ThrowableTranslator.Translate
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.status
import reactor.core.publisher.Mono

internal class ErrorHandler {

    companion object {
        private val logger = LoggerFactory.getLogger(ErrorHandler::class.java)
        private val NOT_FOUND = "not found"
        private val ERROR_RAISED = "error raised"
    }

    fun notFound(request: ServerRequest) = PathNotFoundException(NOT_FOUND).toMono<Throwable>()
            .transform(this::getResponse)

    fun throwableError(throwable: Throwable): Mono<ServerResponse> {
        logger.error(ERROR_RAISED, throwable)
        return throwable.toMono<Throwable>().transform(this::getResponse)
    }

    internal fun <T : Throwable> getResponse(monoError: Mono<T>): Mono<ServerResponse> =
            monoError.transform(Translate::throwable).flatMap {
                status(it.httpStatus) withBody ErrorResponse(it.message)
            }

}
