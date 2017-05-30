package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.PathNotFoundException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.toMono
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.ErrorResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.extractEntity
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@UnitTest
@DisplayName("ErrorHandler Unit Tests")
internal class ErrorHandlerTest : BasicIntegrationTest() {

    companion object {
        const val NOT_FOUND = "not found"
    }

    @Autowired
    lateinit var errorHandler: ErrorHandler

    @Test
    fun notFound() = errorHandler.notFound(mock<ServerRequest>())
            .subscribe(checkResponse(HttpStatus.NOT_FOUND, NOT_FOUND))!!

    private fun checkResponse(httpStatus: HttpStatus, message: String): (ServerResponse) -> Unit = {
        with(it){
            assert.that(statusCode(), equalTo(httpStatus))
            assert.that(extractEntity<ErrorResponse>().message, equalTo(message))
        }
    }

    @Test
    fun throwableError() = errorHandler.throwableError(PathNotFoundException(NOT_FOUND))
            .subscribe(checkResponse(HttpStatus.NOT_FOUND, NOT_FOUND))!!

    @Test
    fun getResponse() = PathNotFoundException(NOT_FOUND).toMono<Throwable>().transform(errorHandler::getResponse)
            .subscribe(checkResponse(HttpStatus.NOT_FOUND, NOT_FOUND))!!
}
