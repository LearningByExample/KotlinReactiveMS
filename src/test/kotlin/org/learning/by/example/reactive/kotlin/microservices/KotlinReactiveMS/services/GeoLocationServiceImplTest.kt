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
        val LOCATION_OK = getMonoFromJsonPath(JSON_OK, GeoLocationResponse::class)
        val LOCATION_NOT_FOUND = getMonoFromJsonPath(JSON_NOT_FOUND, GeoLocationResponse::class)
        val LOCATION_EMPTY = getMonoFromJsonPath(JSON_EMPTY, GeoLocationResponse::class)
        val LOCATION_WRONG_STATUS = getMonoFromJsonPath(JSON_WRONG_STATUS, GeoLocationResponse::class)
        val LOCATION_EXCEPTION: Mono<GeoLocationResponse> = GetGeoLocationException(BAD_EXCEPTION).toMono()
        val BIG_EXCEPTION: Mono<GeoLocationResponse> = RuntimeException(BAD_EXCEPTION).toMono()
    }

    @SpyBean(GeoLocationService::class)
    lateinit var geoLocationServiceImpl: GeoLocationServiceImpl


    @Test
    fun getMockingWebClientTest() {
        geoLocationServiceImpl.webClient = geoLocationServiceImpl.webClient `with mock response` LOCATION_OK

        val geoLocationResponse = GOOGLE_ADDRESS_MONO
                .transform(geoLocationServiceImpl::buildUrl)
                .transform(geoLocationServiceImpl::get).block()
        geoLocationResponse.status `should equal` OK_STATUS

        geoLocationServiceImpl.webClient reset `mock responses`
    }

    @Test
    fun fromAddressTest() {
        (geoLocationServiceImpl `will return` LOCATION_OK).get(any())

        val geographicCoordinates = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO).block()
        geographicCoordinates `should equal` GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG)

        geoLocationServiceImpl reset `mock responses`
    }

    @Test
    fun fromAddressNotFoundTest() {

        (geoLocationServiceImpl `will return` LOCATION_NOT_FOUND).get(any())

        val geographicCoordinates: GeographicCoordinates? = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    it `should be instance of` GeoLocationNotFoundException::class
                    Mono.empty()
                }
                .block()
        geographicCoordinates `should be` null

        geoLocationServiceImpl reset `mock responses`
    }

    @Test
    fun fromAddressEmptyTest() {
        (geoLocationServiceImpl `will return` LOCATION_EMPTY).get(any())

        val geographicCoordinates: GeographicCoordinates? = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    it `should be instance of` GetGeoLocationException::class
                    Mono.empty()
                }
                .block()
        geographicCoordinates `should be` null

        geoLocationServiceImpl reset `mock responses`
    }


    @Test
    fun fromAddressWrongStatusTest() {
        (geoLocationServiceImpl `will return` LOCATION_WRONG_STATUS).get(any())

        val geographicCoordinates: GeographicCoordinates? = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    it `should be instance of` GetGeoLocationException::class
                    Mono.empty()
                }
                .block()
        geographicCoordinates `should be` null

        geoLocationServiceImpl reset `mock responses`
    }

    @Test
    fun fromAddressExceptionTest() {
        (geoLocationServiceImpl `will return` LOCATION_EXCEPTION).get(any())

        val geographicCoordinates: GeographicCoordinates? = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    it `should be instance of` GetGeoLocationException::class
                    Mono.empty()
                }
                .block()
        geographicCoordinates `should be` null

        geoLocationServiceImpl reset `mock responses`
    }

    @Test
    fun fromAddressBigExceptionTest() {
        (geoLocationServiceImpl `will return` BIG_EXCEPTION).get(any())

        val geographicCoordinates: GeographicCoordinates? = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    it `should be instance of` GetGeoLocationException::class
                    Mono.empty()
                }
                .block()
        geographicCoordinates `should be` null

        geoLocationServiceImpl reset `mock responses`
    }


    @Test
    fun buildUrlTest() {
        val url = GOOGLE_ADDRESS_MONO.transform(geoLocationServiceImpl::buildUrl).block()
        url `should equal` geoLocationServiceImpl.endPoint + GOOGLE_ADDRESS_IN_PARAMS
    }

    @Test
    fun buildUrlEmptyAddressTest() {
        val url = "".toMono().transform(geoLocationServiceImpl::buildUrl).onErrorResume {
            it `should be instance of` InvalidParametersException::class
            Mono.empty()
        }.block()
        url `should be` null
    }
}
