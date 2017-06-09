package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services

import com.nhaarman.mockito_kotlin.any
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal to`
import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GetGeoLocationException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GetSunriseSunsetException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeoTimesResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.*
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.OK
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@UnitTest
@DisplayName("SunriseSunsetServiceImplUnit Tests")
private class SunriseSunsetServiceImplTest {

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
        val SUNRISE_SUNSET_OK = getEntityFromJsonPath(JSON_OK, GeoTimesResponse::class)
        val SUNRISE_SUNSET_KO = getEntityFromJsonPath(JSON_KO, GeoTimesResponse::class)
        val SUNRISE_SUNSET_EMPTY = getEntityFromJsonPath(JSON_EMPTY, GeoTimesResponse::class)
        val SUNRISE_SUNSET_ERROR = getEntityFromJsonPath(JSON_EMPTY, GeoTimesResponse::class, INTERNAL_SERVER_ERROR)
        val LOCATION_EXCEPTION: Mono<GeoTimesResponse> = GetGeoLocationException(BAD_EXCEPTION).toMono()
        val BIG_EXCEPTION: Mono<GeoTimesResponse> = RuntimeException(BAD_EXCEPTION).toMono()
    }

    @SpyBean(SunriseSunsetService::class)
    lateinit var serviceImpl: SunriseSunsetServiceImpl

    @Test
    fun getMockingWebClientTest() {
        serviceImpl.webClient = serviceImpl.webClient mocking SUNRISE_SUNSET_OK

        val geoTimesResponse = (serviceImpl.endPoint + GOOGLE_LOCATION_IN_PARAMS).toMono()
                .transform(serviceImpl::get).block()
        with(geoTimesResponse) {
            statusCode `should be` OK
            with(body) {
                status `should equal to` STATUS_OK
                with(results){
                    sunrise `should equal to` SUNRISE_TIME
                    sunset `should equal to` SUNSET_TIME
                }
            }
        }

        serviceImpl.webClient reset `mock responses`
    }

    @Test
    fun getMockingWebClientErrorTest() {
        serviceImpl.webClient = serviceImpl.webClient mocking SUNRISE_SUNSET_ERROR

        val geoTimesResponse = (serviceImpl.endPoint + GOOGLE_LOCATION_IN_PARAMS).toMono()
                .transform(serviceImpl::get)
                .block()

        geoTimesResponse.statusCode `should be` INTERNAL_SERVER_ERROR

        serviceImpl.webClient reset `mock responses`
    }

    @Test
    fun fromGeographicCoordinates() {
        (serviceImpl `will return` SUNRISE_SUNSET_OK).get(any())

        val sunriseSunset = GOOGLE_LOCATION_MONO.transform(serviceImpl::fromGeographicCoordinates).block()
        with(sunriseSunset){
            sunrise `should not be` null
            sunset `should not be` null
        }

        serviceImpl reset `mock responses`
    }

    @Test
    fun fromGeographicCoordinatesKO() {
        (serviceImpl `will return` SUNRISE_SUNSET_KO).get(any())

        val sunriseSunset = GOOGLE_LOCATION_MONO
                .transform(serviceImpl::fromGeographicCoordinates)
                .onErrorResume {
                    it `should be instance of` GetSunriseSunsetException::class
                    Mono.empty()
                }
                .block()
        sunriseSunset `should be` null

        serviceImpl reset `mock responses`
    }

    @Test
    fun fromGeographicCoordinatesEMPTY() {
        (serviceImpl `will return` SUNRISE_SUNSET_EMPTY).get(any())

        val sunriseSunset = GOOGLE_LOCATION_MONO
                .transform(serviceImpl::fromGeographicCoordinates)
                .onErrorResume {
                    it `should be instance of` GetSunriseSunsetException::class
                    Mono.empty()
                }
                .block()
        sunriseSunset `should be` null

        serviceImpl reset `mock responses`
    }

    @Test
    fun fromGeographicCoordinatesException() {
        (serviceImpl `will return` LOCATION_EXCEPTION).get(any())

        val sunriseSunset = GOOGLE_LOCATION_MONO
                .transform(serviceImpl::fromGeographicCoordinates)
                .onErrorResume {
                    it `should be instance of` GetSunriseSunsetException::class
                    Mono.empty()
                }
                .block()
        sunriseSunset `should be` null

        serviceImpl reset `mock responses`
    }

    @Test
    fun fromGeographicCoordinatesBigException() {
        (serviceImpl `will return` BIG_EXCEPTION).get(any())
        val sunriseSunset = GOOGLE_LOCATION_MONO
                .transform(serviceImpl::fromGeographicCoordinates)
                .onErrorResume {
                    it `should be instance of` GetSunriseSunsetException::class
                    Mono.empty()
                }
                .block()
        sunriseSunset `should be` null

        serviceImpl reset `mock responses`
    }

    @Test
    fun buildUrlTest() {
        val url = GOOGLE_LOCATION_MONO.transform(serviceImpl::buildUrl).block()
        url `should equal to` serviceImpl.endPoint + GOOGLE_LOCATION_IN_PARAMS
    }
}
