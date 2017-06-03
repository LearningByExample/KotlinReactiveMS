package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers

import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal to`
import org.amshove.kluent.`should not be`
import org.jsoup.Jsoup
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.BasicIntegrationTest
import org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.test.tags.IntegrationTest
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
@DisplayName("StaticRouter Integration Tests")
internal class StaticRouterTest : BasicIntegrationTest() {

    companion object {
        private val STATIC_PATH = "/index.html"
        private val DEFAULT_TITLE = "Swagger UI"
        private val TITLE_TAG = "title"
    }

    @Autowired
    lateinit var staticRouter: StaticRouter

    @BeforeEach
    fun setup() = bindToRouterFunction(staticRouter.doRoute())

    @Test
    fun doTest() {
        val html: String = get(STATIC_PATH)
        html `should not be` null
        html.length `should be greater than` 0
        verifyTitleIs(html, DEFAULT_TITLE)
    }

    private fun verifyTitleIs(html: String, title: String) {
        val doc = Jsoup.parse(html)
        val element = doc.head().getElementsByTag(TITLE_TAG).get(0)
        val text = element.text()
        text `should equal to` title
    }
}