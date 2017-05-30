package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers

import org.springframework.web.reactive.function.server.router

internal class MainRouter(val apiRouter: ApiRouter, val staticRouter: StaticRouter) {

    fun doRoute() = router {
        routes.addAll(arrayListOf(apiRouter.doRoute(), staticRouter.doRoute()))
    }
}
