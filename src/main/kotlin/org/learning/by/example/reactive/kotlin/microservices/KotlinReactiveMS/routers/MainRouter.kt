package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers

internal class MainRouter(val apiRouter: ApiRouter, val staticRouter: StaticRouter) {

  fun doRoute() = apiRouter.doRoute().andOther(staticRouter.doRoute())

}


