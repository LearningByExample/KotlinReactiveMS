package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.application

import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.LocationRequest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.LocationResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.SystemTest
import org.springframework.boot.web.server.LocalServerPort


@SystemTest
@DisplayName("KotlinReactiveMsApplication System Tests")
private class KotlinReactiveMsApplicationTests : BasicIntegrationTest() {

    companion object {
        const val GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA"
        const val API_LOCATION = "/api/location"
    }

    @LocalServerPort
    var port: Int = 0

    @BeforeEach
    fun setup() = bindToPort(port)

    @Test
    fun getLocation() {
        val locationResponse: LocationResponse = get(url = "$API_LOCATION/$GOOGLE_ADDRESS")
        locationResponse.geographicCoordinates `should not be` null
    }

    @Test
    fun postLocation() {
        val locationResponse: LocationResponse = post(url = API_LOCATION, value = LocationRequest(GOOGLE_ADDRESS))
        locationResponse.geographicCoordinates `should not be` null
    }
}
