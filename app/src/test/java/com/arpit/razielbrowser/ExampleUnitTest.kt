package com.arpit.razielbrowser

import com.arpit.newsapp3.APIService
import com.arpit.newsapp3.newsService
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun GETarticlesbyTags() {
        runBlocking {
            val articles = newsService.apiService.getHeadlines().execute()
            assertNotNull(articles.body()?.articles)
        }
    }
}