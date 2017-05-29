package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.toMono
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.LocationRequest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.LocationResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.SunriseSunset
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.GeoLocationService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.SunriseSunsetService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.extractEntity
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@UnitTest
@DisplayName("ApiHandler Unit Tests")
private class ApiHandlerTests : BasicIntegrationTest() {

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
                assert.that(latitude, equalTo(GOOGLE_LAT))
                assert.that(longitude, equalTo(GOOGLE_LNG))
            }
            with(sunriseSunset)
            {
                assert.that(sunrise, equalTo(SUNRISE_TIME))
                assert.that(sunset, equalTo(SUNSET_TIME))
            }
        }
    }

    @Test
    fun sunriseSunsetTest() {

        doReturn(SUNRISE_SUNSET).whenever(sunriseSunsetService).fromGeographicCoordinates(any())

        val serverRequest = mock<ServerRequest>()
        doReturn(GOOGLE_ADDRESS).whenever(serverRequest).pathVariable(ADDRESS_VARIABLE)

        with(apiHandler.sunriseSunset(GOOGLE_LOCATION).block())
        {
            assert.that(sunrise, equalTo(SUNRISE_TIME))
            assert.that(sunset, equalTo(SUNSET_TIME))
        }

        reset(sunriseSunsetService)
    }

    @Test
    fun serverResponseTest() {
        GOOGLE_LOCATION_MONO.and(SUNRISE_SUNSET, ::LocationResponse)
                .transform(apiHandler::serverResponse)
                .subscribe(this::verifyServerResponse)
    }

    private fun verifyServerResponse(serverResponse: ServerResponse) {

        assert.that(serverResponse.statusCode(), equalTo(HttpStatus.OK))

        val locationResponse: LocationResponse = serverResponse.extractEntity()

        verifyLocationResponse(locationResponse)
    }

    @Test
    fun buildResponseTest() {

        doReturn(GOOGLE_LOCATION_MONO).whenever(geoLocationService).fromAddress(any())
        doReturn(SUNRISE_SUNSET).whenever(sunriseSunsetService).fromGeographicCoordinates(any())

        val locationResponse = GOOGLE_ADDRESS.toMono().publish(apiHandler::buildResponse).block()

        verifyLocationResponse(locationResponse)

        reset(geoLocationService)
        reset(sunriseSunsetService)
    }

    @Test
    fun getLocationTest() {

        doReturn(GOOGLE_LOCATION_MONO).whenever(geoLocationService).fromAddress(any())
        doReturn(SUNRISE_SUNSET).whenever(sunriseSunsetService).fromGeographicCoordinates(any())

        val serverRequest = mock<ServerRequest>()
        doReturn(GOOGLE_ADDRESS).whenever(serverRequest).pathVariable(ADDRESS_VARIABLE)

        val serverResponse = apiHandler.getLocation(serverRequest).block()

        verifyServerResponse(serverResponse)

        reset(geoLocationService)
        reset(sunriseSunsetService)
    }

    @Test
    fun postLocationTest() {

        doReturn(GOOGLE_LOCATION_MONO).whenever(geoLocationService).fromAddress(any())
        doReturn(SUNRISE_SUNSET).whenever(sunriseSunsetService).fromGeographicCoordinates(any())

        val serverRequest = mock<ServerRequest>()
        doReturn(GOOGLE_ADDRESS_REQUEST).whenever(serverRequest).bodyToMono(any<Class<LocationRequest>>())

        val serverResponse = apiHandler.postLocation(serverRequest).block()

        verifyServerResponse(serverResponse)

        reset(geoLocationService)
        reset(sunriseSunsetService)
    }
}
