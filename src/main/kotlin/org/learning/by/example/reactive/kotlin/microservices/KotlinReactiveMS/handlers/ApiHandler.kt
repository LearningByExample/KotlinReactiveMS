package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.extract
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.withBody
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.LocationRequest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.LocationResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.GeoLocationService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.SunriseSunsetService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

internal class ApiHandler(val geoLocationService: GeoLocationService, val sunriseSunsetService: SunriseSunsetService,
                          val errorHandler: ErrorHandler) {

    private companion object {
        const val ADDRESS = "address"
    }

    internal fun getLocation(request: ServerRequest) =
            request.pathVariable(ADDRESS).toMono()
                    .transform(this::buildResponse)
                    .transform(this::serverResponse)
                    .onErrorResume(errorHandler::throwableError)!!

    internal fun postLocation(request: ServerRequest) =
            request.extract<LocationRequest>()
                    .map(LocationRequest::address)
                    .transform(this::buildResponse)
                    .transform(this::serverResponse)
                    .onErrorResume(errorHandler::throwableError)!!

    internal fun buildResponse(address: Mono<String>) =
            address.transform(geoLocationService::fromAddress).zipWhen(this::sunriseSunset, ::LocationResponse)

    internal fun sunriseSunset(geographicCoordinates: GeographicCoordinates) =
            geographicCoordinates.toMono().transform(sunriseSunsetService::fromGeographicCoordinates)

    internal fun serverResponse(locationResponseMono: Mono<LocationResponse>): Mono<ServerResponse> =
            locationResponseMono.flatMap { ok() withBody it }
}
