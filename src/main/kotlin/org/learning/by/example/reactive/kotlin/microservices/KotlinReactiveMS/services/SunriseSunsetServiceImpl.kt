package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GetSunriseSunsetException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeoTimesResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.SunriseSunset
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

open internal class SunriseSunsetServiceImpl(val endPoint: String, var webClient: WebClient = WebClient.create())
    : SunriseSunsetService {

    private companion object {
        const val TODAY_DATE = "today"
        const val NOT_FORMATTED = "0"
        const val ERROR_GETTING_DATA = "error getting sunrise and sunset"
        const val SUNRISE_RESULT_NOT_OK = "sunrise and sunset result was not OK"
        const val STATUS_OK = "OK"
    }

    override fun fromGeographicCoordinates(geographicCoordinatesMono: Mono<GeographicCoordinates>) =
            geographicCoordinatesMono
                    .transform(this::buildUrl)
                    .transform(this::get)
                    .onErrorResume { GetSunriseSunsetException(ERROR_GETTING_DATA, it).toMono() }
                    .transform(this::createResult)!!

    open internal fun buildUrl(geographicCoordinatesMono: Mono<GeographicCoordinates>) =
            geographicCoordinatesMono.flatMap { (latitude, longitude) ->
                "$endPoint?lat=$latitude&lng=$longitude&date=$TODAY_DATE&formatted=$NOT_FORMATTED".toMono()
            }

    open internal fun get(urlMono: Mono<String>) =
            urlMono.flatMap {
                webClient.get()
                        .uri(it)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .flatMap { it.bodyToMono(GeoTimesResponse::class.java) }
            }!!

    open internal fun createResult(geoTimesResponseMono: Mono<GeoTimesResponse>) =
            geoTimesResponseMono.flatMap {
                with(it){
                    if (status == STATUS_OK) with(results) { SunriseSunset(sunrise, sunset).toMono() }
                    else GetSunriseSunsetException(SUNRISE_RESULT_NOT_OK).toMono()
                }
            }
}
