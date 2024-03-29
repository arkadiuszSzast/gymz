package com.szastarek.gymz.adapter.rest

import com.szastarek.gymz.module
import com.szastarek.gymz.support.createZitadelIdToken
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.config.mergeWith
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

class OauthLoginTest : StringSpec() {

    init {

        "should login via oauth" {
            // arrange
            val zitadelBaseUrl = "http://zitadel-test.com"
            val idToken = createZitadelIdToken()
            val tokenResponse = OauthTokenResponse(
                accessToken = UUID.randomUUID().toString(),
                tokenType = "Bearer",
                expiresIn = 43199,
                idToken = idToken.value,
                refreshToken = UUID.randomUUID().toString(),
            )
            testApplication {
                environment {
                    config = ApplicationConfig("application.conf").mergeWith(
                        MapApplicationConfig(
                            "zitadel.authorizeUrl" to "$zitadelBaseUrl/oauth/v2/authorize",
                            "zitadel.accessTokenUrl" to "$zitadelBaseUrl/oauth/v2/token",
                            "zitadel.callbackUrl" to "/auth/callback",
                        ),
                    )
                }
                application {
                    module(this@testApplication.client)
                }

                externalServices {
                    hosts(zitadelBaseUrl) {
                        install(ContentNegotiation) {
                            json()
                        }
                        routing {
                            get("/oauth/v2/authorize") {
                                val redirectUri = call.parameters["redirect_uri"]!!
                                val state = call.parameters["state"]!!
                                call.respondRedirect("$redirectUri?code=sTJqjhoXuH4GdrQr_NtUKL68kllM5o8uJzXnLPHabXe3dw&state=$state")
                            }
                            post("/oauth/v2/token") {
                                call.respond(HttpStatusCode.OK, tokenResponse)
                            }
                        }
                    }
                }

                // act && assert
                client.get("/auth/login")
                    .status.shouldBe(HttpStatusCode.OK)
            }
        }
    }
}

@Serializable
data class OauthTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("id_token")
    val idToken: String,
    @SerialName("refresh_token")
    val refreshToken: String,
)
