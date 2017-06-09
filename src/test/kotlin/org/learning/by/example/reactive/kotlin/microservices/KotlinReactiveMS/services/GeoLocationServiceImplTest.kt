package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services

import com.nhaarman.mockito_kotlin.any
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GeoLocationNotFoundException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GetGeoLocationException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.InvalidParametersException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeoLocationResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.*
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.OK
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@UnitTest
@DisplayName("GeoLocationServiceImpl Unit Tests")
private class GeoLocationServiceImplTest {

    private companion object {
        const val GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA"
        const val GOOGLE_ADDRESS_IN_PARAMS = "?address=" + GOOGLE_ADDRESS
        val GOOGLE_ADDRESS_MONO = GOOGLE_ADDRESS.toMono()
        const val GOOGLE_LAT = 37.4224082
        const val GOOGLE_LNG = -122.0856086
        const val OK_STATUS = "OK"
        const val BAD_EXCEPTION = "bad exception"
        const val JSON_OK = "/json/GeoLocationResponse_OK.json"
        const val JSON_NOT_FOUND = "/json/GeoLocationResponse_NOT_FOUND.json"
        const val JSON_EMPTY = "/json/GeoLocationResponse_EMPTY.json"
        const val JSON_WRONG_STATUS = "/json/GeoLocationResponse_WRONG_STATUS.json"
        val LOCATION_OK = getEntityFromJsonPath(JSON_OK, GeoLocationResponse::class)
        val LOCATION_NOT_FOUND = getEntityFromJsonPath(JSON_NOT_FOUND, GeoLocationResponse::class)
        val LOCATION_EMPTY = getEntityFromJsonPath(JSON_EMPTY, GeoLocationResponse::class)
        val LOCATION_ERROR = getEntityFromJsonPath(JSON_EMPTY, GeoLocationResponse::class, INTERNAL_SERVER_ERROR)
        val LOCATION_WRONG_STATUS = getEntityFromJsonPath(JSON_WRONG_STATUS, GeoLocationResponse::class)
        val LOCATION_EXCEPTION: Mono<GeoLocationResponse> = GetGeoLocationException(BAD_EXCEPTION).toMono()
        val BIG_EXCEPTION: Mono<GeoLocationResponse> = RuntimeException(BAD_EXCEPTION).toMono()
    }

    @SpyBean(GeoLocationService::class)
    lateinit var serviceImpl: GeoLocationServiceImpl


    @Test
    fun getMockingWebClientTest() {
        serviceImpl.webClient = serviceImpl.webClient mocking LOCATION_OK

        val geoLocationResponse = GOOGLE_ADDRESS_MONO
                .transform(serviceImpl::buildUrl)
                .transform(serviceImpl::get).block()

        geoLocationResponse.statusCode `should equal` OK

        geoLocationResponse.body.status `should equal` OK_STATUS

        serviceImpl.webClient reset `mock responses`
    }

    @Test
    fun getMockingWebClientErrorTest() {
        serviceImpl.webClient = serviceImpl.webClient mocking LOCATION_ERROR

        val geoLocationResponse = GOOGLE_ADDRESS_MONO
                .transform(serviceImpl::buildUrl)
                .transform(serviceImpl::get)
                .block()

        geoLocationResponse.statusCode `should be` INTERNAL_SERVER_ERROR

        serviceImpl.webClient reset `mock responses`
    }

    @Test
    fun fromAddressTest() {

        (serviceImpl `will return` LOCATION_OK).get(any())

        val geographicCoordinates = serviceImpl.fromAddress(GOOGLE_ADDRESS_MONO).block()
        geographicCoordinates `should equal` GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG)

        serviceImpl reset `mock responses`
    }

    @Test
    fun fromAddressNotFoundTest() {

        (serviceImpl `will return` LOCATION_NOT_FOUND).get(any())

        val geographicCoordinates: GeographicCoordinates? = serviceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    it `should be instance of` GeoLocationNotFoundException::class
                    Mono.empty()
                }
                .block()
        geographicCoordinates `should be` null

        serviceImpl reset `mock responses`
    }

    @Test
    fun fromAddressEmptyTest() {
        (serviceImpl `will return` LOCATION_EMPTY).get(any())

        val geographicCoordinates: GeographicCoordinates? = serviceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    it `should be instance of` GetGeoLocationException::class
                    Mono.empty()
                }
                .block()
        geographicCoordinates `should be` null

        serviceImpl reset `mock responses`
    }


    @Test
    fun fromAddressWrongStatusTest() {
        (serviceImpl `will return` LOCATION_WRONG_STATUS).get(any())

        val geographicCoordinates: GeographicCoordinates? = serviceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    it `should be instance of` GetGeoLocationException::class
                    Mono.empty()
                }
                .block()
        geographicCoordinates `should be` null

        serviceImpl reset `mock responses`
    }

    @Test
    fun fromAddressExceptionTest() {
        (serviceImpl `will return` LOCATION_EXCEPTION).get(any())

        val geographicCoordinates: GeographicCoordinates? = serviceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    it `should be instance of` GetGeoLocationException::class
                    Mono.empty()
                }
                .block()
        geographicCoordinates `should be` null

        serviceImpl reset `mock responses`
    }

    @Test
    fun fromAddressBigExceptionTest() {
        (serviceImpl `will return` BIG_EXCEPTION).get(any())

        val geographicCoordinates: GeographicCoordinates? = serviceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    it `should be instance of` GetGeoLocationException::class
                    Mono.empty()
                }
                .block()
        geographicCoordinates `should be` null

        serviceImpl reset `mock responses`
    }


    @Test
    fun buildUrlTest() {
        val url = GOOGLE_ADDRESS_MONO.transform(serviceImpl::buildUrl).block()
        url `should equal` serviceImpl.endPoint + GOOGLE_ADDRESS_IN_PARAMS
    }

    @Test
    fun buildUrlEmptyAddressTest() {
        val url = "".toMono().transform(serviceImpl::buildUrl).onErrorResume {
            it `should be instance of` InvalidParametersException::class
            Mono.empty()
        }.block()
        url `should be` null
    }
}
