package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.routers

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isEmptyString
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
        assert.that(html, !isEmptyString)
        verifyTitleIs(html, DEFAULT_TITLE)
    }

    private fun verifyTitleIs(html: String, title: String) {
        val doc = Jsoup.parse(html)
        val element = doc.head().getElementsByTag(TITLE_TAG).get(0)
        val text = element.text()
        assert.that(text, equalTo(title))
    }
}