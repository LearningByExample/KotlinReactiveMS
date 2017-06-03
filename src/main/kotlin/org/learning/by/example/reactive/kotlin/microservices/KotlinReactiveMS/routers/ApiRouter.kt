package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers.ApiHandler
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers.ErrorHandler
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router


internal class ApiRouter(val handler: ApiHandler, val errorHandler: ErrorHandler) {

    private companion object {
        const private val API_PATH = "/api"
        const val LOCATION_PATH = "/location"
        const val ADDRESS_ARG = "/{address}"
        const val ANY_PATH = "/**"
        const val LOCATION_WITH_ADDRESS_PATH = "$LOCATION_PATH$ADDRESS_ARG"
    }

    fun doRoute() = router {
        (accept(MediaType.APPLICATION_JSON_UTF8) and API_PATH).nest {
            GET(LOCATION_WITH_ADDRESS_PATH)(handler::getLocation)
            POST(LOCATION_PATH)(handler::postLocation)
            path(ANY_PATH)(errorHandler::notFound)
        }
    }
}
