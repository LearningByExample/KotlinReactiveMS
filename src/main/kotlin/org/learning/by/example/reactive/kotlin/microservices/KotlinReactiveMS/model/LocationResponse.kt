package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model

internal data class LocationResponse(var geographicCoordinates: GeographicCoordinates = GeographicCoordinates(),
                                     var sunriseSunset: SunriseSunset = SunriseSunset())
