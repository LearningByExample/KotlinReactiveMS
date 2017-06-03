package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.*
import org.springframework.web.reactive.function.client.ClientResponse
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
fun <T : Any> getMonoFromJsonPath(jsonPath: String, type: KClass<T>) = getResourceAsText(jsonPath)
        .getObjectFromJson(type).toMono()

infix fun <T, K> T.willReturn(value: K) = doReturn(value).whenever(this)!!
infix fun <T, K> T.`will return`(value: K) = this willReturn value

internal class MockResponseKeyword
internal val `mock responses` = MockResponseKeyword()
@Suppress("UNUSED_PARAMETER")
internal infix fun<T:Any> T.reset(keyword : MockResponseKeyword) = reset(this)

fun <T : Any> mockWebClient(webClient: WebClient, mono: Mono<T>?): WebClient {

    val client = spy(webClient)
    val uriSpec = mock<WebClient.UriSpec<*>>()
    (client `will return` uriSpec).get()

    val headerSpec = mock<WebClient.RequestHeadersSpec<*>>()
    (uriSpec `will return` headerSpec).uri(any<String>())
    (headerSpec `will return` headerSpec).accept(any())

    val clientResponse = mock<ClientResponse>()
    (clientResponse `will return` mono).bodyToMono(any<Class<Mono<T>>>())
    (headerSpec `will return` clientResponse.toMono()).exchange()

    return client
}

infix fun <T: WebClient,K : Any> T.withMockResponse(value : Mono<K>?) = mockWebClient(this, value)
infix fun <T: WebClient,K : Any> T.`with mock response`(value : Mono<K>?) = this.withMockResponse(value)