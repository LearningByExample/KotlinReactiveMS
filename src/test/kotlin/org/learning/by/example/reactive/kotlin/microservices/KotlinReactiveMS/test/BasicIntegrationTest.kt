package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.RouterFunction
import kotlin.reflect.KClass

internal abstract class BasicIntegrationTest {

    var webTestClient: WebTestClient? = null

    protected fun bindToRouterFunction(router: RouterFunction<*>) {
        this.webTestClient = WebTestClient.bindToRouterFunction(router).build()
    }

    protected fun bindToPort(port: Int) {
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
    }

    fun <T : Any> get(url: String, httpStatus: HttpStatus = HttpStatus.OK, type: KClass<T>) =
            webTestClient?.get()
                    ?.uri(url)
                    ?.accept(MediaType.APPLICATION_JSON_UTF8)
                    ?.exchange()
                    ?.expectStatus()?.isEqualTo(httpStatus)
                    ?.expectBody(type.java)
                    ?.returnResult()?.responseBody!!
}
