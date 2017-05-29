package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.router

import com.natpryce.hamkrest.assertion.assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.LocationRequest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model.LocationResponse
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers.ApiRouter
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.isNull
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.IntegrationTest
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
@DisplayName("ApiRouter Integration Tests")
private class ApiRouterTest : BasicIntegrationTest() {

    @Autowired
    lateinit var apiRouter: ApiRouter

    companion object {
        const val GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA"
        const val API_LOCATION = "/api/location"
    }

    @BeforeEach
    fun setup() = bindToRouterFunction(apiRouter.doRoute())

    @Test
    fun getLocation() {
        val locationResponse = get(url = "$API_LOCATION/$GOOGLE_ADDRESS", type = LocationResponse::class)
        assert.that(locationResponse.geographicCoordinates, !isNull())
    }

    @Test
    fun postLocation() {
        val locationResponse = post(url = API_LOCATION, value = LocationRequest(GOOGLE_ADDRESS),
                type = LocationResponse::class)

        assert.that(locationResponse.geographicCoordinates, !isNull())
    }
}
