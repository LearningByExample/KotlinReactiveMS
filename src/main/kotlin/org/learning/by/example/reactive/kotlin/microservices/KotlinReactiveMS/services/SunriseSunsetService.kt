package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.SunriseSunset
import reactor.core.publisher.Mono

internal interface SunriseSunsetService {
    fun fromGeographicCoordinates(geographicCoordinatesMono: Mono<GeographicCoordinates>): Mono<SunriseSunset>
}
