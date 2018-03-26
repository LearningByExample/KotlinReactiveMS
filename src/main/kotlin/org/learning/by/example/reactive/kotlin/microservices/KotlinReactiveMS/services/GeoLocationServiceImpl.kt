package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services

import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GeoLocationNotFoundException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GetGeoLocationException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.InvalidParametersException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeoLocationResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

open internal class GeoLocationServiceImpl(val endPoint: String, var webClient: WebClient = WebClient.create())
    : GeoLocationService {

    private companion object {
        const val MISSING_ADDRESS = "missing address"
        const val OK_STATUS = "OK"
        const val ZERO_RESULTS = "ZERO_RESULTS"
        const val ERROR_GETTING_LOCATION = "error getting location"
        const val ERROR_NOT_200 = "response was not 200"
        const val ADDRESS_NOT_FOUND = "address not found"
    }

    override fun fromAddress(addressMono: Mono<String>) =
            addressMono
                    .transform(this::buildUrl)
                    .transform(this::get)
                    .onErrorResume { GetGeoLocationException(ERROR_GETTING_LOCATION, it).toMono() }
                    .transform(this::geometryLocation)!!

    open internal fun buildUrl(addressMono: Mono<String>) =
            addressMono.flatMap {
                if (it != "") (endPoint + "?address=$it").toMono()
                else InvalidParametersException(MISSING_ADDRESS).toMono()
            }

    open internal fun get(urlMono: Mono<String>) =
            urlMono.flatMap {
                webClient.get()
                        .uri(it)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange().flatMap { it.toEntity<GeoLocationResponse>() }
            }

    open internal fun geometryLocation(responseMono: Mono<ResponseEntity<GeoLocationResponse>>) =
            responseMono.flatMap {
                if (it.statusCode != HttpStatus.OK)
                    GetGeoLocationException(ERROR_NOT_200).toMono()
                else with(it.body) {
                    when (status) {
                        OK_STATUS -> with(results[0].geometry.location) { GeographicCoordinates(lat, lng).toMono() }
                        ZERO_RESULTS -> GeoLocationNotFoundException(ADDRESS_NOT_FOUND).toMono()
                        else -> GetGeoLocationException(ERROR_GETTING_LOCATION).toMono()
                    }
                }
            }
}
