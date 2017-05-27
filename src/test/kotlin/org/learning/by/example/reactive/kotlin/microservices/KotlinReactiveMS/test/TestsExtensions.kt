package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.natpryce.hamkrest.Matcher
import com.nhaarman.mockito_kotlin.*
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.extensions.toMono
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.anyString
import org.mockito.internal.matchers.InstanceOf
import org.mockito.internal.progress.ThreadSafeMockingProgress
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.EntityResponse
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> ServerResponse.extractEntity() =
        (this as EntityResponse<Mono<T>>).entity().block()!!

fun getResourceAsText(resource: String) = Unit.javaClass.getResource(resource).readText()
fun <T : Any> String.getObjectFromJson(type: KClass<T>) = ObjectMapper().readValue(this, type.java)!!
fun <T : Any> getMonoFromJsonPath(jsonPath: String, type: KClass<T>) = getResourceAsText(jsonPath)
        .getObjectFromJson(type).toMono()

fun Any?.isNullValue(): Boolean = this == null
fun isNull() = Matcher(Any?::isNullValue)

private fun reportMatcher(matcher: ArgumentMatcher<*>) {
    ThreadSafeMockingProgress.mockingProgress().argumentMatcherStorage.reportMatcher(matcher)
}

fun anyClass(): Class<Any> {
    reportMatcher(InstanceOf(Any::class.java, "<any object>"))
    return Any::class.java
}

fun <T : Any> mockWebClient(webClient: WebClient, mono: Mono<T>?): WebClient {

    val client = spy(webClient)
    val uriSpec = mock<WebClient.UriSpec<*>>()

    doReturn(uriSpec).whenever(client).get()

    val headerSpec = mock<WebClient.RequestHeadersSpec<*>>()
    doReturn(headerSpec).whenever(uriSpec).uri(anyString())
    doReturn(headerSpec).whenever(headerSpec).accept(any())

    val clientResponse = mock<ClientResponse>()
    doReturn(mono).whenever(clientResponse).bodyToMono(anyClass())
    doReturn(Mono.just(clientResponse)).whenever(headerSpec).exchange()

    return client
}
