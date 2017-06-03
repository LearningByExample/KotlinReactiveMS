package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.isEmptyString
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.LocationResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.SunriseSunset
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.GeoLocationService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services.SunriseSunsetService
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.isNull
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.IntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import reactor.core.publisher.toMono

@IntegrationTest
@DisplayName("MainRouter Integration Tests")
internal class MainRouterTest  : BasicIntegrationTest() {

    companion object {
        const val GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA"
        const val API_LOCATION = "/api/location"
        const val SUNRISE_TIME = "2017-05-21T12:53:56+00:00"
        const val SUNSET_TIME = "2017-05-22T03:16:05+00:00"
        const val GOOGLE_LAT = 37.4224082
        const val GOOGLE_LNG = -122.0856086
        private val GOOGLE_LOCATION = GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG).toMono()
        private val SUNRISE_SUNSET = SunriseSunset(SUNRISE_TIME, SUNSET_TIME).toMono()
        private val STATIC_PATH = "/index.html"
    }

    @Autowired
    lateinit var mainRouter : MainRouter

    @SpyBean
    lateinit private var geoLocationService: GeoLocationService

    @SpyBean
    lateinit private var sunriseSunsetService: SunriseSunsetService

    @BeforeEach
    fun setup() = bindToRouterFunction(mainRouter.doRoute())

    @Test
    fun staticRouterTest() {
        val html : String = get(STATIC_PATH)
        assert.that(html, !isEmptyString)
    }

    @Test
    fun apiRouterTest() {
        doReturn(GOOGLE_LOCATION).whenever(geoLocationService).fromAddress(any())
        doReturn(SUNRISE_SUNSET).whenever(sunriseSunsetService).fromGeographicCoordinates(any())

        val locationResponse : LocationResponse = get(url = "${API_LOCATION}/${GOOGLE_ADDRESS}")
        assert.that(locationResponse.geographicCoordinates, !isNull())

        reset(geoLocationService)
        reset(sunriseSunsetService)
    }

}
