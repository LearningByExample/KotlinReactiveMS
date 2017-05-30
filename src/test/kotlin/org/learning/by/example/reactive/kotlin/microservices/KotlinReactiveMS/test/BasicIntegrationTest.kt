package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RouterFunction

internal abstract class BasicIntegrationTest {

    lateinit var webTestClient: WebTestClient

    protected fun bindToRouterFunction(router: RouterFunction<*>) {
        this.webTestClient = WebTestClient.bindToRouterFunction(router).build()
    }

    protected fun bindToPort(port: Int) {
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
    }

    inline fun <reified T : Any> get(url: String, mediaType: MediaType = MediaType.APPLICATION_JSON_UTF8,
                                     httpStatus: HttpStatus = HttpStatus.OK) =
            webTestClient.get()
                    ?.uri(url)
                    ?.accept(mediaType)
                    ?.exchange()
                    ?.expectStatus()?.isEqualTo(httpStatus)
                    ?.expectBody(T::class.java)
                    ?.returnResult()?.responseBody!!

    inline fun <T : Any, reified K: Any> post(url: String, value: T, httpStatus: HttpStatus = HttpStatus.OK) =
            webTestClient.post()
                    ?.uri(url)
                    ?.body(BodyInserters.fromObject(value))
                    ?.accept(MediaType.APPLICATION_JSON_UTF8)
                    ?.exchange()
                    ?.expectStatus()?.isEqualTo(httpStatus)
                    ?.expectBody(K::class.java)
                    ?.returnResult()?.responseBody!!
}
