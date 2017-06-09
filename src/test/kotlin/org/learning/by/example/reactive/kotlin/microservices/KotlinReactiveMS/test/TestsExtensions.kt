package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.EntityResponse
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> ServerResponse.extractEntity() = (this as EntityResponse<Mono<T>>).entity().block()!!

fun getResourceAsText(resource: String) = Unit.javaClass.getResource(resource).readText()
fun <T : Any> String.getObjectFromJson(type: KClass<T>) = ObjectMapper().readValue(this, type.java)!!
fun <T : Any> getEntityFromJsonPath(jsonPath: String, type: KClass<T>, httpStatus: HttpStatus = HttpStatus.OK)
        = ResponseEntity<T>(getResourceAsText(jsonPath).getObjectFromJson(type), httpStatus).toMono()

infix fun <T, K> T.willReturn(value: K) = doReturn(value).whenever(this)!!
infix fun <T, K> T.`will return`(value: K) = this willReturn value

internal class MockResponseKeyword

internal val `mock responses` = MockResponseKeyword()
@Suppress("UNUSED_PARAMETER")
internal infix fun <T : Any> T.reset(keyword: MockResponseKeyword) = reset(this)

fun <T : Any> mockWebClient(webClient: WebClient, mono: Mono<ResponseEntity<T>>): WebClient {

    val client = spy(webClient)
    val uriSpec = mock<WebClient.UriSpec<*>>()
    (client `will return` uriSpec).get()

    val headerSpec = mock<WebClient.RequestHeadersSpec<*>>()
    (uriSpec `will return` headerSpec).uri(any<String>())
    (headerSpec `will return` headerSpec).accept(any())

    val responseSpec = mock<WebClient.ResponseSpec>()
    val value = mono.block()
    (headerSpec `will return` responseSpec).retrieve()
    (responseSpec `will return` mono).toEntity(value.body.javaClass)

    return client
}

infix fun <T : WebClient, K : Any> T.mocking(value: Mono<ResponseEntity<K>>) = mockWebClient(this, value)

