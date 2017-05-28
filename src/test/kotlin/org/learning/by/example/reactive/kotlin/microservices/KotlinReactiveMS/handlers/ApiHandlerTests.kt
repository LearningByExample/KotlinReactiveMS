package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.handlers

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isNullOrEmptyString
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.HelloResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.LocationResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.extractEntity
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest

@UnitTest
@DisplayName("ApiHandler Unit Tests")
private class ApiHandlerTests : BasicIntegrationTest() {

    private companion object {
        const val GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA"
        const val GOOGLE_LAT = 37.4224082
        const val GOOGLE_LNG = -122.0856086
    }

    @Autowired
    lateinit var apiHandler: ApiHandler

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
        doReturn(GOOGLE_ADDRESS).whenever(serverRequest).pathVariable(any())

        val serverResponse = apiHandler.getLocation(serverRequest).block()

        assert.that(serverResponse.statusCode(), equalTo(HttpStatus.OK))

        val locationResponse: LocationResponse = serverResponse.extractEntity()
        assert.that(locationResponse.geographicCoordinates.latitude, equalTo(GOOGLE_LAT))
        assert.that(locationResponse.geographicCoordinates.longitude, equalTo(GOOGLE_LNG))
        assert.that(locationResponse.sunriseSunset.sunrise, !isNullOrEmptyString)
        assert.that(locationResponse.sunriseSunset.sunset, !isNullOrEmptyString)

    }

}
