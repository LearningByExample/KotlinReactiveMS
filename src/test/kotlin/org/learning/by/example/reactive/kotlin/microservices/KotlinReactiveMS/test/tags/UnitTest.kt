package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.application.KotlinReactiveMsApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@Tag("UnitTest")
@SpringBootTest(classes = arrayOf(KotlinReactiveMsApplication::class))
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
internal annotation class UnitTest
