package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

@UnitTest
@DisplayName("GeoLocationServiceImpl Unit Tests")
internal class GeoLocationServiceImplTest {

    private companion object {
        const val GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA"
        const val GOOGLE_ADDRESS_IN_PARAMS = "?address=" + GOOGLE_ADDRESS
        val GOOGLE_ADDRESS_MONO = Mono.just(GOOGLE_ADDRESS)!!
        const val GOOGLE_LAT = 37.4224082
        const val GOOGLE_LNG = -122.0856086
        const val OK_STATUS = "OK"
    }

    @Autowired
    lateinit var geoLocationServiceImpl: GeoLocationServiceImpl

    @Test
    fun fromAddressTest() {
        val geographicCoordinates = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO).block()

        assert.that(geographicCoordinates, equalTo(GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG)))
    }

    @Test
    fun buildUrlTest() {
        val url = GOOGLE_ADDRESS_MONO.transform(geoLocationServiceImpl::buildUrl).block()

        assert.that(url, equalTo(geoLocationServiceImpl.endPoint + GOOGLE_ADDRESS_IN_PARAMS))
    }

    @Test
    fun getTest() {
        val geoLocationResponse = GOOGLE_ADDRESS_MONO
                .transform(geoLocationServiceImpl::buildUrl)
                .transform(geoLocationServiceImpl::get).block()

        assert.that(geoLocationResponse.status, equalTo(OK_STATUS))

        with(geoLocationResponse.results[0].geometry.location) {
            assert.that(lat, equalTo(GOOGLE_LAT))
            assert.that(lng, equalTo(GOOGLE_LNG))
        }
    }
}
