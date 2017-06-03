package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers

import com.nhaarman.mockito_kotlin.any
import org.amshove.kluent.`should equal to`
import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.*
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.GeoLocationService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.SunriseSunsetService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.`mock responses`
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.`will return`
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.reset
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.IntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpStatus
import reactor.core.publisher.toMono

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
        (geoLocationService `will return` GOOGLE_LOCATION_MONO).fromAddress(any())
        (sunriseSunsetService `will return` SUNRISE_SUNSET).fromGeographicCoordinates(any())

        val locationResponse : LocationResponse = get(url = "${API_LOCATION}/${GOOGLE_ADDRESS}")
        locationResponse.geographicCoordinates `should not be` null

        geoLocationService reset `mock responses`
        sunriseSunsetService reset `mock responses`
    }

    @Test
    fun getLocationRouteNotFound() {
        val errorResponse : ErrorResponse = get(url = API_BAD_URL, httpStatus = HttpStatus.NOT_FOUND)
        errorResponse.message `should equal to` NOT_FOUND
    }


    @Test
    fun postLocation() {
        (geoLocationService `will return` GOOGLE_LOCATION_MONO).fromAddress(any())
        (sunriseSunsetService `will return` SUNRISE_SUNSET).fromGeographicCoordinates(any())

        val locationResponse : LocationResponse = post(url = API_LOCATION, value = LocationRequest(GOOGLE_ADDRESS))
        locationResponse.geographicCoordinates `should not be` null

        geoLocationService reset `mock responses`
        sunriseSunsetService reset `mock responses`
    }

    @Test
    fun postWrongObject() {
        val errorResponse : ErrorResponse = post(url = API_LOCATION,httpStatus = HttpStatus.BAD_REQUEST ,
                value = WrongObject())
        errorResponse `should not be` null
    }

    @Test
    fun postLocationRouteNotFound() {
        val errorResponse : ErrorResponse = post(url = API_BAD_URL, httpStatus = HttpStatus.NOT_FOUND,
                value = LocationRequest(GOOGLE_ADDRESS))
        errorResponse.message `should equal to` NOT_FOUND
    }
}
