package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.isNullOrEmptyString
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.toMono
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.boot.test.mock.mockito.SpyBean

@UnitTest
@DisplayName("SunriseSunsetServiceImplUnit Tests")
internal class SunriseSunsetServiceImplTest {

    private companion object {
        const val GOOGLE_LAT = 37.4224082
        const val GOOGLE_LNG = -122.0856086
        val GOOGLE_LOCATION = GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG)
        val GOOGLE_LOCATION_MONO = GOOGLE_LOCATION.toMono()
    }

    @SpyBean(SunriseSunsetService::class)
    lateinit var sunriseSunsetServiceImpl: SunriseSunsetServiceImpl

    @Test
    fun fromGeographicCoordinates() {
        val sunriseSunset = GOOGLE_LOCATION_MONO
                .transform(sunriseSunsetServiceImpl::fromGeographicCoordinates).block()

        assert.that(sunriseSunset.sunrise, !isNullOrEmptyString)
        assert.that(sunriseSunset.sunset, !isNullOrEmptyString)
    }
}
