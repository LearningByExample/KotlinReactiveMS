package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.with
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.HelloResponse
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok

class ApiHandler {

    fun getHello(req: ServerRequest) = ok() with HelloResponse("world")
}
