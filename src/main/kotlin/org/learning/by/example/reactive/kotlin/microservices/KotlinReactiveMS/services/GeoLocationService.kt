package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import reactor.core.publisher.Mono

internal interface GeoLocationService {
    fun fromAddress(addressMono: Mono<String>): Mono<GeographicCoordinates>
}
