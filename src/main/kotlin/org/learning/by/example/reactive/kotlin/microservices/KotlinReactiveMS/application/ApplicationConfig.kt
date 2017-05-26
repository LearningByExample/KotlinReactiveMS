package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.application

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers.ApiHandler
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers.ApiRouter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.EnableWebFlux

@Configuration
@EnableWebFlux
internal class ApplicationConfig {

    @Bean
    fun apiHandler() = ApiHandler()

    @Bean
    fun apiRouter(apiHandler: ApiHandler) = ApiRouter(apiHandler)

    @Bean
    fun apiRouterFunction(apiRouter: ApiRouter) = apiRouter.doRoute()
}