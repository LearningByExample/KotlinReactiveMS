package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.router

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.toMono
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.*
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers.ApiRouter
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.GeoLocationService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.SunriseSunsetService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.isNull
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.IntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpStatus

@IntegrationTest
@DisplayName("ApiRouter Integration Tests")
private class ApiRouterTest : BasicIntegrationTest() {

    companion object {
        const val GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA"
        const val API_LOCATION = "/api/location"
        const val API_BAD_URL = "/api/bad/url"
        const val SUNRISE_TIME = "2017-05-21T12:53:56+00:00"
        const val SUNSET_TIME = "2017-05-22T03:16:05+00:00"
        const val GOOGLE_LAT = 37.4224082
        const val GOOGLE_LNG = -122.0856086
        const val NOT_FOUND = "not found"
        private val GOOGLE_LOCATION = GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG)
        private val GOOGLE_LOCATION_MONO = GOOGLE_LOCATION.toMono()
        private val SUNRISE_SUNSET = SunriseSunset(SUNRISE_TIME, SUNSET_TIME).toMono()
    }

    private data class WrongObject(val wrongValue : String = GOOGLE_ADDRESS)

    @Autowired
    lateinit var apiRouter: ApiRouter

    @SpyBean
    lateinit private var geoLocationService: GeoLocationService

    @SpyBean
    lateinit private var sunriseSunsetService: SunriseSunsetService

    @BeforeEach
    fun setup() = bindToRouterFunction(apiRouter.doRoute())

    @Test
    fun getLocation() {
        doReturn(GOOGLE_LOCATION_MONO).whenever(geoLocationService).fromAddress(any())
        doReturn(SUNRISE_SUNSET).whenever(sunriseSunsetService).fromGeographicCoordinates(any())

        val locationResponse = get(url = "${API_LOCATION}/${GOOGLE_ADDRESS}", type = LocationResponse::class)
        assert.that(locationResponse.geographicCoordinates, !isNull())

        reset(geoLocationService)
        reset(sunriseSunsetService)
    }

    @Test
    fun getLocationRouteNotFound() {
        val errorResponse = get(url = API_BAD_URL, httpStatus = HttpStatus.NOT_FOUND, type = ErrorResponse::class)
        assert.that(errorResponse.message, equalTo(NOT_FOUND))
    }


    @Test
    fun postLocation() {

        doReturn(GOOGLE_LOCATION_MONO).whenever(geoLocationService).fromAddress(any())
        doReturn(SUNRISE_SUNSET).whenever(sunriseSunsetService).fromGeographicCoordinates(any())

        val locationResponse = post(url = API_LOCATION, value = LocationRequest(GOOGLE_ADDRESS),
                type = LocationResponse::class)

        assert.that(locationResponse.geographicCoordinates, !isNull())

        reset(geoLocationService)
        reset(sunriseSunsetService)
    }

    @Test
    fun postWrongObject() {
        val errorResponse = post(url = API_LOCATION,httpStatus = HttpStatus.BAD_REQUEST , value = WrongObject(),
                type = ErrorResponse::class)

        assert.that(errorResponse.message, !isNull())
    }

    @Test
    fun postLocationRouteNotFound() {
        val errorResponse = post(url = API_BAD_URL, httpStatus = HttpStatus.NOT_FOUND, value = LocationRequest(GOOGLE_ADDRESS),
                type = ErrorResponse::class)

        assert.that(errorResponse.message, equalTo(NOT_FOUND))
    }
}
