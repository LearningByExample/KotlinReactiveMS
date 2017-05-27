package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.application

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers.ApiHandler
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers.ApiRouter
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.GeoLocationService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.GeoLocationServiceImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.EnableWebFlux

@Configuration
@EnableWebFlux
internal class ApplicationConfig {

    @Bean
    internal fun apiHandler() = ApiHandler()

    @Bean
    internal fun apiRouter(apiHandler: ApiHandler) = ApiRouter(apiHandler)

    @Bean
    internal fun apiRouterFunction(apiRouter: ApiRouter) = apiRouter.doRoute()

    @Bean
    internal fun geoLocationService(@Value("\${GeoLocationServiceImpl.endPoint}") endPoint: String): GeoLocationService
            = GeoLocationServiceImpl(endPoint)
}