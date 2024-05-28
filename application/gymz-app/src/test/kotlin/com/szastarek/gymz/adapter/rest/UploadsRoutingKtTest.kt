package com.szastarek.gymz.adapter.rest

import com.szastarek.gymz.support.IntegrationTest
import com.szastarek.gymz.support.upload
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.util.toByteArray

class UploadsRoutingKtTest : IntegrationTest() {

    private val fileContent = ClassLoader.getSystemResource("static-files/example-text.txt").readBytes()

    init {
        "should upload file" { client ->
            // arrange
            val authToken = authenticate().authToken

            // act
            val response = client.upload(authToken, fileContent)

            // assert
            response.status shouldBe HttpStatusCode.Accepted
            val location = response.headers[HttpHeaders.Location].shouldNotBeNull()
            HttpClient(CIO).get(location).bodyAsChannel().toByteArray() shouldBe fileContent
        }

        "cannot upload file when not authenticated" { client ->
            // arrange & act
            val response = client.post(UPLOADS_API_PREFIX) {
                setBody(fileContent)
            }

            // assert
            response should {
                it.status shouldBe HttpStatusCode.Unauthorized
                it.headers[HttpHeaders.Location].shouldBeNull()
            }
        }
    }
}
