package com.szastarek.gymz.adapter.rest

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication

class StatusPagesTest : StringSpec({

    "should return 400 on BadRequestException" {
        testApplication {
            environment {
                developmentMode = false
            }
            routing {
                get("/bad-request-exception") {
                    throw BadRequestException("invalid request")
                }
            }

            val response = client.get("/bad-request-exception")
            response.status shouldBe HttpStatusCode.BadRequest
        }
    }
})
