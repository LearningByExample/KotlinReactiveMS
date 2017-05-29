package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isA
import com.natpryce.hamkrest.isNullOrEmptyString
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GetGeoLocationException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GetSunriseSunsetException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.toMono
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeoTimesResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.getMonoFromJsonPath
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.isNull
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.mockWebClient
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.boot.test.mock.mockito.SpyBean
import reactor.core.publisher.Mono

@UnitTest
@DisplayName("SunriseSunsetServiceImplUnit Tests")
internal class SunriseSunsetServiceImplTest {

    private companion object {
        const val STATUS_OK = "OK"
        const val BAD_EXCEPTION = "bad exception"
        const val GOOGLE_LAT = 37.4224082
        const val GOOGLE_LNG = -122.0856086
        const val SUNRISE_TIME = "2017-05-21T12:53:56+00:00"
        const val SUNSET_TIME = "2017-05-22T03:16:05+00:00"
        val GOOGLE_LOCATION = GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG)
        val GOOGLE_LOCATION_MONO = GOOGLE_LOCATION.toMono()
        const val GOOGLE_LOCATION_IN_PARAMS = "?lat=$GOOGLE_LAT&lng=$GOOGLE_LNG&date=today&formatted=0"
        const val JSON_OK = "/json/GeoTimesResponse_OK.json"
        const val JSON_KO = "/json/GeoTimesResponse_KO.json"
        const val JSON_EMPTY = "/json/GeoTimesResponse_EMPTY.json"
        val SUNRISE_SUNSET_OK = getMonoFromJsonPath(JSON_OK, GeoTimesResponse::class)
        val SUNRISE_SUNSET_KO = getMonoFromJsonPath(JSON_KO, GeoTimesResponse::class)
        val SUNRISE_SUNSET_EMPTY = getMonoFromJsonPath(JSON_EMPTY, GeoTimesResponse::class)
        val LOCATION_EXCEPTION: Mono<GeoTimesResponse> = GetGeoLocationException(BAD_EXCEPTION).toMono()
        val BIG_EXCEPTION: Mono<GeoTimesResponse> = RuntimeException(BAD_EXCEPTION).toMono()
    }

    @SpyBean(SunriseSunsetService::class)
    lateinit var sunriseSunsetServiceImpl: SunriseSunsetServiceImpl

    @Test
    fun getMockingWebClientTest() {
        sunriseSunsetServiceImpl.webClient = mockWebClient(sunriseSunsetServiceImpl.webClient, SUNRISE_SUNSET_OK)

        val geoTimesResponse = (sunriseSunsetServiceImpl.endPoint + GOOGLE_LOCATION_IN_PARAMS).toMono()
                .transform(sunriseSunsetServiceImpl::get).block()

        assert.that(geoTimesResponse.status, equalTo(STATUS_OK))
        assert.that(geoTimesResponse.results.sunrise, equalTo(SUNRISE_TIME))
        assert.that(geoTimesResponse.results.sunset, equalTo(SUNSET_TIME))

        reset(sunriseSunsetServiceImpl.webClient)
    }

    @Test
    fun fromGeographicCoordinates() {
        doReturn(SUNRISE_SUNSET_OK).whenever(sunriseSunsetServiceImpl).get(any())

        val sunriseSunset = GOOGLE_LOCATION_MONO.transform(sunriseSunsetServiceImpl::fromGeographicCoordinates).block()

        assert.that(sunriseSunset.sunrise, !isNullOrEmptyString)
        assert.that(sunriseSunset.sunset, !isNullOrEmptyString)

        reset(sunriseSunsetServiceImpl)
    }

    @Test
    fun fromGeographicCoordinatesKO() {
        doReturn(SUNRISE_SUNSET_KO).whenever(sunriseSunsetServiceImpl).get(any())

        val sunriseSunset = GOOGLE_LOCATION_MONO
                .transform(sunriseSunsetServiceImpl::fromGeographicCoordinates)
                .onErrorResume {
                    assert.that(it, isA<GetSunriseSunsetException>())
                    Mono.empty()
                }
                .block()

        assert.that(sunriseSunset, isNull())

        reset(sunriseSunsetServiceImpl)
    }

    @Test
    fun fromGeographicCoordinatesEMPTY() {
        doReturn(SUNRISE_SUNSET_EMPTY).whenever(sunriseSunsetServiceImpl).get(any())

        val sunriseSunset = GOOGLE_LOCATION_MONO
                .transform(sunriseSunsetServiceImpl::fromGeographicCoordinates)
                .onErrorResume {
                    assert.that(it, isA<GetSunriseSunsetException>())
                    Mono.empty()
                }
                .block()

        assert.that(sunriseSunset, isNull())

        reset(sunriseSunsetServiceImpl)
    }

    @Test
    fun fromGeographicCoordinatesException() {
        doReturn(LOCATION_EXCEPTION).whenever(sunriseSunsetServiceImpl).get(any())

        val sunriseSunset = GOOGLE_LOCATION_MONO
                .transform(sunriseSunsetServiceImpl::fromGeographicCoordinates)
                .onErrorResume {
                    assert.that(it, isA<GetSunriseSunsetException>())
                    Mono.empty()
                }
                .block()

        assert.that(sunriseSunset, isNull())

        reset(sunriseSunsetServiceImpl)
    }

    @Test
    fun fromGeographicCoordinatesBigException() {
        doReturn(BIG_EXCEPTION).whenever(sunriseSunsetServiceImpl).get(any())

        val sunriseSunset = GOOGLE_LOCATION_MONO
                .transform(sunriseSunsetServiceImpl::fromGeographicCoordinates)
                .onErrorResume {
                    assert.that(it, isA<GetSunriseSunsetException>())
                    Mono.empty()
                }
                .block()

        assert.that(sunriseSunset, isNull())

        reset(sunriseSunsetServiceImpl)
    }

    @Test
    fun buildUrlTest() {
        val url = GOOGLE_LOCATION_MONO.transform(sunriseSunsetServiceImpl::buildUrl).block()

        assert.that(url, equalTo(sunriseSunsetServiceImpl.endPoint + GOOGLE_LOCATION_IN_PARAMS))
    }
}
