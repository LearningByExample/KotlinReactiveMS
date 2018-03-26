package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal to`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GeoLocationNotFoundException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GetGeoLocationException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GetSunriseSunsetException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.*
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.GeoLocationService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.SunriseSunsetService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.*
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@UnitTest
@DisplayName("ApiHandler Unit Tests")
private class ApiHandlerTest : BasicIntegrationTest() {

    private companion object {
        const val ADDRESS_VARIABLE = "address"
        const val GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA"
        const val SUNRISE_TIME = "2017-05-21T12:53:56+00:00"
        const val SUNSET_TIME = "2017-05-22T03:16:05+00:00"
        const val GOOGLE_LAT = 37.4224082
        const val GOOGLE_LNG = -122.0856086
        const val NOT_FOUND = "not found"
        const val CANT_GET_LOCATION = "cant get location"
        const val CANT_GET_SUNRISE_SUNSET = "can't get sunrise sunset"
        private val GOOGLE_LOCATION = GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG)
        private val GOOGLE_LOCATION_MONO = GOOGLE_LOCATION.toMono()
        private val SUNRISE_SUNSET = SunriseSunset(SUNRISE_TIME, SUNSET_TIME).toMono()
        private val GOOGLE_ADDRESS_REQUEST = LocationRequest(GOOGLE_ADDRESS).toMono()
        private val LOCATION_NOT_FOUND : Mono<GeographicCoordinates> = GeoLocationNotFoundException(NOT_FOUND).toMono()
        private val LOCATION_EXCEPTION : Mono<GeographicCoordinates> = GetGeoLocationException(CANT_GET_LOCATION).toMono()
        private val SUNRISE_SUNSET_ERROR : Mono<SunriseSunset> = GetSunriseSunsetException(CANT_GET_SUNRISE_SUNSET).toMono()
    }

    @Autowired
    lateinit var apiHandler: ApiHandler

    @SpyBean
    lateinit private var geoLocationService: GeoLocationService

    @SpyBean
    lateinit private var sunriseSunsetService: SunriseSunsetService

    @Test
    fun combineTest() {
        GOOGLE_LOCATION_MONO.and(SUNRISE_SUNSET, ::LocationResponse)
                .subscribe(this::verifyLocationResponse)
    }

    private fun verifyLocationResponse(locationResponse: LocationResponse) {
        with(locationResponse) {
            with(geographicCoordinates)
            {
                latitude `should equal to` GOOGLE_LAT
                longitude `should equal to` GOOGLE_LNG
            }
            with(sunriseSunset)
            {
                sunrise `should equal to` SUNRISE_TIME
                sunset `should equal to` SUNSET_TIME
            }
        }
    }

    @Test
    fun sunriseSunsetTest() {
        (sunriseSunsetService `will return` SUNRISE_SUNSET).fromGeographicCoordinates(any())
        val serverRequest = mock<ServerRequest>()
        (serverRequest `will return` GOOGLE_ADDRESS).pathVariable(ADDRESS_VARIABLE)

        with(apiHandler.sunriseSunset(GOOGLE_LOCATION).block())
        {
            sunrise `should equal to` SUNRISE_TIME
            sunset `should equal to` SUNSET_TIME
        }

        sunriseSunsetService reset `mock responses`
    }

    @Test
    fun serverResponseTest() {
        GOOGLE_LOCATION_MONO.and(SUNRISE_SUNSET, ::LocationResponse)
                .transform(apiHandler::serverResponse)
                .subscribe(this::verifyServerResponse)
    }

    private fun verifyServerResponse(serverResponse: ServerResponse) {
        serverResponse.statusCode() `should be` HttpStatus.OK
        val locationResponse: LocationResponse = serverResponse.extractEntity()
        verifyLocationResponse(locationResponse)
    }

    @Test
    fun buildResponseTest() {
        (geoLocationService `will return` GOOGLE_LOCATION_MONO).fromAddress(any())
        (sunriseSunsetService `will return` SUNRISE_SUNSET).fromGeographicCoordinates(any())

        val locationResponse = GOOGLE_ADDRESS.toMono().publish(apiHandler::buildResponse).block()
        verifyLocationResponse(locationResponse)

        geoLocationService reset `mock responses`
        sunriseSunsetService reset `mock responses`
    }

    @Test
    fun getLocationTest() {
        (geoLocationService `will return` GOOGLE_LOCATION_MONO).fromAddress(any())
        (sunriseSunsetService `will return` SUNRISE_SUNSET).fromGeographicCoordinates(any())
        val serverRequest = mock<ServerRequest>()
        (serverRequest `will return` GOOGLE_ADDRESS).pathVariable(ADDRESS_VARIABLE)

        val serverResponse = apiHandler.getLocation(serverRequest).block()
        verifyServerResponse(serverResponse)

        geoLocationService reset `mock responses`
        sunriseSunsetService reset `mock responses`
    }

    @Test
    fun postLocationTest() {
        (geoLocationService `will return` GOOGLE_LOCATION_MONO).fromAddress(any())
        (sunriseSunsetService `will return` SUNRISE_SUNSET).fromGeographicCoordinates(any())
        val serverRequest = mock<ServerRequest>()
        (serverRequest `will return` GOOGLE_ADDRESS_REQUEST).bodyToMono(any<Class<LocationRequest>>())

        val serverResponse = apiHandler.postLocation(serverRequest).block()
        verifyServerResponse(serverResponse)

        geoLocationService reset `mock responses`
        sunriseSunsetService reset `mock responses`
    }

    @Test
    fun getLocationNotFoundTest(){
        (geoLocationService `will return` LOCATION_NOT_FOUND).fromAddress(any())
        (sunriseSunsetService `will return` SUNRISE_SUNSET).fromGeographicCoordinates(any())
        val serverRequest = mock<ServerRequest>()
        (serverRequest `will return` GOOGLE_ADDRESS).pathVariable(ADDRESS_VARIABLE)

        val serverResponse = apiHandler.getLocation(serverRequest).block()
        serverResponse.statusCode() `should be` HttpStatus.NOT_FOUND
        val errorResponse : ErrorResponse = serverResponse.extractEntity()
        errorResponse.message `should equal to` NOT_FOUND

        geoLocationService reset `mock responses`
        sunriseSunsetService reset `mock responses`
    }

    @Test
    fun getLocationErrorSunriseSunsetTest(){
        (geoLocationService `will return` GOOGLE_LOCATION_MONO).fromAddress(any())
        (sunriseSunsetService `will return` SUNRISE_SUNSET_ERROR).fromGeographicCoordinates(any())
        val serverRequest = mock<ServerRequest>()
        (serverRequest `will return` GOOGLE_ADDRESS).pathVariable(ADDRESS_VARIABLE)

        val serverResponse = apiHandler.getLocation(serverRequest).block()
        serverResponse.statusCode() `should be` HttpStatus.INTERNAL_SERVER_ERROR
        val errorResponse : ErrorResponse = serverResponse.extractEntity()
        errorResponse.message `should equal to` CANT_GET_SUNRISE_SUNSET

        geoLocationService reset `mock responses`
        sunriseSunsetService reset `mock responses`
    }

    @Test
    fun getLocationBothServiceErrorTest(){
        (geoLocationService `will return` LOCATION_EXCEPTION).fromAddress(any())
        (sunriseSunsetService `will return` SUNRISE_SUNSET_ERROR).fromGeographicCoordinates(any())
        val serverRequest = mock<ServerRequest>()
        (serverRequest `will return` GOOGLE_ADDRESS).pathVariable(ADDRESS_VARIABLE)

        val serverResponse = apiHandler.getLocation(serverRequest).block()
        serverResponse.statusCode() `should be` HttpStatus.INTERNAL_SERVER_ERROR
        val errorResponse : ErrorResponse = serverResponse.extractEntity()
        errorResponse.message `should equal to` CANT_GET_LOCATION

        geoLocationService reset `mock responses`
        sunriseSunsetService reset `mock responses`
    }
}
