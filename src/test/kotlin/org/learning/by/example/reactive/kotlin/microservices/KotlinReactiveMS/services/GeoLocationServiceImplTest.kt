package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.services

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isA
import com.nhaarman.mockito_kotlin.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GeoLocationNotFoundException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.GetGeoLocationException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.exceptions.InvalidParametersException
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.toMono
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeoLocationResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.GeographicCoordinates
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.getMonoFromJsonPath
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.isNull
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.mockWebClient
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.boot.test.mock.mockito.SpyBean
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
        const val BAD_EXCEPTION = "bad exception"
        const val JSON_OK = "/json/GeoLocationResponse_OK.json"
        const val JSON_NOT_FOUND = "/json/GeoLocationResponse_NOT_FOUND.json"
        const val JSON_EMPTY = "/json/GeoLocationResponse_EMPTY.json"
        const val JSON_WRONG_STATUS = "/json/GeoLocationResponse_WRONG_STATUS.json"
        val LOCATION_OK = getMonoFromJsonPath(JSON_OK, GeoLocationResponse::class)
        val LOCATION_NOT_FOUND = getMonoFromJsonPath(JSON_NOT_FOUND, GeoLocationResponse::class)
        val LOCATION_EMPTY = getMonoFromJsonPath(JSON_EMPTY, GeoLocationResponse::class)
        val LOCATION_WRONG_STATUS = getMonoFromJsonPath(JSON_WRONG_STATUS, GeoLocationResponse::class)
        val LOCATION_EXCEPTION: Mono<GeoLocationResponse> = GetGeoLocationException(BAD_EXCEPTION).toMono()
        val BIG_EXCEPTION: Mono<GeoLocationResponse> = RuntimeException(BAD_EXCEPTION).toMono()
    }

    @SpyBean(GeoLocationService::class)
    lateinit var geoLocationServiceImpl: GeoLocationServiceImpl


    @Test
    fun getMockingWebClientTest() {

        geoLocationServiceImpl.webClient = mockWebClient(geoLocationServiceImpl.webClient, LOCATION_OK)

        val geoLocationResponse = GOOGLE_ADDRESS_MONO
                .transform(geoLocationServiceImpl::buildUrl)
                .transform(geoLocationServiceImpl::get).block()

        assert.that(geoLocationResponse.status, equalTo(OK_STATUS))

        reset(geoLocationServiceImpl.webClient)
    }

    @Test
    fun fromAddressTest() {

        doReturn(LOCATION_OK).whenever(geoLocationServiceImpl).get(any())

        val geographicCoordinates = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO).block()

        assert.that(geographicCoordinates, equalTo(GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG)))

        reset(geoLocationServiceImpl)
    }

    @Test
    fun fromAddressNotFoundTest() {

        doReturn(LOCATION_NOT_FOUND).whenever(geoLocationServiceImpl).get(any())

        val geographicCoordinates: GeographicCoordinates? = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    assert.that(it, isA<GeoLocationNotFoundException>())
                    Mono.empty()
                }
                .block()

        assert.that(geographicCoordinates, isNull())

        verify(geoLocationServiceImpl, times(1)).fromAddress(any())
        verify(geoLocationServiceImpl, times(1)).get(any())
        verify(geoLocationServiceImpl, times(1)).buildUrl(any())
        verify(geoLocationServiceImpl, times(1)).geometryLocation(any())

        reset(geoLocationServiceImpl)
    }

    @Test
    fun fromAddressEmptyTest() {

        doReturn(LOCATION_EMPTY).whenever(geoLocationServiceImpl).get(any())

        val geographicCoordinates: GeographicCoordinates? = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    assert.that(it, isA<GetGeoLocationException>())
                    Mono.empty()
                }
                .block()

        assert.that(geographicCoordinates, isNull())

        verify(geoLocationServiceImpl, times(1)).fromAddress(any())
        verify(geoLocationServiceImpl, times(1)).get(any())
        verify(geoLocationServiceImpl, times(1)).buildUrl(any())
        verify(geoLocationServiceImpl, times(1)).geometryLocation(any())

        reset(geoLocationServiceImpl)
    }


    @Test
    fun fromAddressWrongStatusTest() {

        doReturn(LOCATION_WRONG_STATUS).whenever(geoLocationServiceImpl).get(any())

        val geographicCoordinates: GeographicCoordinates? = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    assert.that(it, isA<GetGeoLocationException>())
                    Mono.empty()
                }
                .block()

        assert.that(geographicCoordinates, isNull())

        verify(geoLocationServiceImpl, times(1)).fromAddress(any())
        verify(geoLocationServiceImpl, times(1)).get(any())
        verify(geoLocationServiceImpl, times(1)).buildUrl(any())
        verify(geoLocationServiceImpl, times(1)).geometryLocation(any())

        reset(geoLocationServiceImpl)
    }

    @Test
    fun fromAddressExceptionTest() {

        doReturn(LOCATION_EXCEPTION).whenever(geoLocationServiceImpl).get(any())

        val geographicCoordinates: GeographicCoordinates? = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    assert.that(it, isA<GetGeoLocationException>())
                    Mono.empty()
                }
                .block()

        assert.that(geographicCoordinates, isNull())

        verify(geoLocationServiceImpl, times(1)).fromAddress(any())
        verify(geoLocationServiceImpl, times(1)).get(any())
        verify(geoLocationServiceImpl, times(1)).buildUrl(any())
        verify(geoLocationServiceImpl, times(1)).geometryLocation(any())

        reset(geoLocationServiceImpl)
    }

    @Test
    fun fromAddressBigExceptionTest() {

        doReturn(BIG_EXCEPTION).whenever(geoLocationServiceImpl).get(any())

        val geographicCoordinates: GeographicCoordinates? = geoLocationServiceImpl.fromAddress(GOOGLE_ADDRESS_MONO)
                .onErrorResume {
                    assert.that(it, isA<GetGeoLocationException>())
                    Mono.empty()
                }
                .block()

        assert.that(geographicCoordinates, isNull())

        verify(geoLocationServiceImpl, times(1)).fromAddress(any())
        verify(geoLocationServiceImpl, times(1)).get(any())
        verify(geoLocationServiceImpl, times(1)).buildUrl(any())
        verify(geoLocationServiceImpl, times(1)).geometryLocation(any())

        reset(geoLocationServiceImpl)
    }


    @Test
    fun buildUrlTest() {
        val url = GOOGLE_ADDRESS_MONO.transform(geoLocationServiceImpl::buildUrl).block()

        assert.that(url, equalTo(geoLocationServiceImpl.endPoint + GOOGLE_ADDRESS_IN_PARAMS))
    }

    @Test
    fun buildUrlEmptyAddressTest() {
        val url = "".toMono().transform(geoLocationServiceImpl::buildUrl).onErrorResume {
            assert.that(it, isA<InvalidParametersException>())
            Mono.empty()
        }.block()

        assert.that(url, isNull())
    }
}

