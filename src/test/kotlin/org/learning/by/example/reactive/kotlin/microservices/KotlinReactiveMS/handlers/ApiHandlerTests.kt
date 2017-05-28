package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isNullOrEmptyString
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.toMono
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.HelloResponse
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

@UnitTest
@DisplayName("ApiHandler Unit Tests")
private class ApiHandlerTests : BasicIntegrationTest() {

    private companion object {
        const val ADDRESS_VARIABLE = "address"
        const val GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA"
        const val SUNRISE_TIME = "12:55:17 PM"
        const val SUNSET_TIME = "3:14:28 AM"
        const val GOOGLE_LAT = 37.4224082
        const val GOOGLE_LNG = -122.0856086
        const val NOT_FOUND = "not found"
        const val CANT_GET_LOCATION = "cant get location"
        const val CANT_GET_SUNRISE_SUNSET = "can't get sunrise sunset"
        private val GOOGLE_LOCATION = GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG).toMono()
        private val SUNRISE_SUNSET = SunriseSunset(SUNRISE_TIME, SUNSET_TIME).toMono()
    }

    @Autowired
    lateinit var apiHandler: ApiHandler

    @SpyBean
    lateinit private var geoLocationService: GeoLocationService

    @SpyBean
    lateinit private var sunriseSunsetService: SunriseSunsetService

    @Test
    fun apiHandlerTest() {

        val serverRequest = mock<ServerRequest>()
        val serverResponseMono = apiHandler.getHello(serverRequest)

        serverResponseMono.subscribe {
            assert.that(it.statusCode(), equalTo(HttpStatus.OK))

            val helloResponse: HelloResponse = it.extractEntity()
            assert.that(helloResponse.hello, equalTo("world"))
        }
    }

    @Test
    fun getLocationTest() {

        val serverRequest = mock<ServerRequest>()
        doReturn(GOOGLE_ADDRESS).whenever(serverRequest).pathVariable(ADDRESS_VARIABLE)

        val serverResponse = apiHandler.getLocation(serverRequest).block()

        assert.that(serverResponse.statusCode(), equalTo(HttpStatus.OK))

        val locationResponse: LocationResponse = serverResponse.extractEntity()

        with(locationResponse) {
            with(geographicCoordinates)
            {
                assert.that(latitude, equalTo(GOOGLE_LAT))
                assert.that(longitude, equalTo(GOOGLE_LNG))
            }
            with(sunriseSunset)
            {
                assert.that(sunrise, !isNullOrEmptyString)
                assert.that(sunset, !isNullOrEmptyString)
            }
        }
    }
}
