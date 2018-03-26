package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.application

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.UnitTest

@UnitTest
@DisplayName("main Unit Tests")
private class mainTest : BasicIntegrationTest() {

    @Test
    fun mainTest() {
        main(arrayOf())
    }
}
