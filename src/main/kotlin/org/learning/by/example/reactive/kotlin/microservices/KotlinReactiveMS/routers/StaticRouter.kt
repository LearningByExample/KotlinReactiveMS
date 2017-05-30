package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers

import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.router

internal class StaticRouter {

    companion object{
        private val ROUTE = "/**"
        private val PUBLIC = "public/"
    }

    fun doRoute() = router {
        resources(ROUTE, ClassPathResource(PUBLIC))
    }
}