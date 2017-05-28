package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.toMono
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.SunriseSunset
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

open internal class SunriseSunsetServiceImpl(val endPoint: String, var webClient: WebClient = WebClient.create())
    : SunriseSunsetService {

    private companion object{
        const val BEGIN_PARAMETERS = "?"
        const val NEXT_PARAMETER = "&"
        const val EQUALS = "="
        const val LATITUDE_PARAMETER = "lat" + EQUALS
        const val LONGITUDE_PARAMETER = "lng" + EQUALS
        const val DATE_PARAMETER = "date" + EQUALS
        const val TODAY_DATE = "today"
        const val FORMATTED_PARAMETER = "formatted" + EQUALS
        const val NOT_FORMATTED = "0"
        const val ERROR_GETTING_DATA = "error getting sunrise and sunset"
        const val SUNRISE_RESULT_NOT_OK = "sunrise result was not OK"
        const val STATUS_OK = "OK"
    }

    override fun fromGeographicCoordinates(geographicCoordinatesMono: Mono<GeographicCoordinates>): Mono<SunriseSunset> {
        return SunriseSunset("2017-05-21T12:53:56+00:00","2017-05-22T03:16:05+00:00").toMono()
    }
}
