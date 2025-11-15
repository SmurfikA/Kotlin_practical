package com.example

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {

    class ApplicationTest {
        @Test
        fun testRoot() = testApplication {
            application { configureRouting() }
            fun tasksCanBeFoundByPriority() = testApplication {
                application {
                    module()
                }

                val response = client.get("/tasks/byPriority/Medium")
                val body = response.bodyAsText()

                val response = client.get("/")
                assertEquals(HttpStatusCode.OK, response.status)
                assertEquals("Hello World!", response.bodyAsText())
                assertContains(body, "Mow the lawn")
                assertContains(body, "Paint the fence")
            }

            @Test
            fun testNewEndpoint() = testApplication {
                application { configureRouting() }
                fun invalidPriorityProduces400() = testApplication {
                    application {
                        module()
                    }

                    val response = client.get("/test1")
                    assertEquals(HttpStatusCode.OK, response.status)
                    assertEquals("html", response.contentType()?.contentSubtype)
                    assertContains(response.bodyAsText(), "Hello From Ktor")
                    val response = client.get("/tasks/byPriority/Invalid")
                    assertEquals(HttpStatusCode.BadRequest, response.status)
                }

                @Test
                fun unusedPriorityProduces404() = testApplication {
                    application {
                        module()
                    }

                    val response = client.get("/tasks/byPriority/Vital")
                    assertEquals(HttpStatusCode.NotFound, response.status)
                }

                @Test
                fun newTasksCanBeAdded() = testApplication {
                    application {
                        module()
                    }

                    val response1 = client.post("/tasks") {
                        header(
                            HttpHeaders.ContentType,
                            ContentType.Application.FormUrlEncoded.toString()
                        )
                        setBody(
                            listOf(
                                "name" to "swimming",
                                "description" to "Go to the beach",
                                "priority" to "Low"
                            ).formUrlEncode()
                        )
                    }

                    assertEquals(HttpStatusCode.NoContent, response1.status)

                    val response2 = client.get("/tasks")
                    assertEquals(HttpStatusCode.OK, response2.status)
                    val body = response2.bodyAsText()

                    assertContains(body, "swimming")
                    assertContains(body, "Go to the beach")
                }
            }
        }
    }
}