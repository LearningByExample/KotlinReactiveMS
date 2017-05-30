package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.application

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers.ApiHandler
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers.ErrorHandler
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers.ApiRouter
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers.MainRouter
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers.StaticRouter
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.GeoLocationService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.GeoLocationServiceImpl
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.SunriseSunsetService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.SunriseSunsetServiceImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.EnableWebFlux

@Configuration
@EnableWebFlux
internal class ApplicationConfig {

    @Bean
    internal fun apiHandler(geoLocationService: GeoLocationService, sunsetService: SunriseSunsetService,
                            errorHandler: ErrorHandler) = ApiHandler(geoLocationService, sunsetService, errorHandler)

    @Bean
    internal fun errorHandler() = ErrorHandler()

    @Bean
    internal fun apiRouter(apiHandler: ApiHandler, errorHandler: ErrorHandler) = ApiRouter(apiHandler, errorHandler)

    @Bean
    internal fun staticRouter() = StaticRouter()

    @Bean
    internal fun mainRouter(apiRouter: ApiRouter, staticRouter: StaticRouter) = MainRouter(apiRouter, staticRouter)

    @Bean
    internal fun mainRouterFunction(mainRouter: MainRouter) = mainRouter.doRoute()

    @Bean
    internal fun geoLocationService(@Value("\${GeoLocationServiceImpl.endPoint}") endPoint: String): GeoLocationService
            = GeoLocationServiceImpl(endPoint)

    @Bean
    internal fun gunriseSunsetService(@Value("\${SunriseSunsetServiceImpl.endPoint}") endPoint: String): SunriseSunsetService
            = SunriseSunsetServiceImpl(endPoint)
}
