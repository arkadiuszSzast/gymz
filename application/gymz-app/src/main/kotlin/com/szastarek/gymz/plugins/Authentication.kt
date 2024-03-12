package com.szastarek.gymz.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.szastarek.gymz.config.JwtAuthTokenProperties
import com.szastarek.gymz.config.ZitadelProperties
import com.szastarek.gymz.shared.http.ProblemHttpErrorResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.oauth
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

object AuthMethods {
    const val OAUTH = "oauth"
    const val JWT = "jwt"
}

fun Route.oauthAuthenticate(build: Route.() -> Unit) {
    authenticate(AuthMethods.OAUTH) { build() }
}

fun Route.jwtAuthenticate(build: Route.() -> Unit) {
    authenticate(AuthMethods.JWT) { build() }
}

fun Application.configureAuthentication(jwtAuthTokenProperties: JwtAuthTokenProperties, zitadelProperties: ZitadelProperties) {
    install(Authentication) {
        oauth(AuthMethods.OAUTH) {
            client = HttpClient {}
            urlProvider = { "http://localhost:8081/auth/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "gymz",
                    authorizeUrl = "${zitadelProperties.baseUrl}/oauth/v2/authorize",
                    accessTokenUrl = "${zitadelProperties.baseUrl}/oauth/v2/token",
                    clientId = zitadelProperties.clientId.value,
                    accessTokenRequiresBasicAuth = false,
                    requestMethod = HttpMethod.Post,
                    clientSecret = zitadelProperties.clientSecret.value,
                    defaultScopes =
                    listOf(
                        "openid",
                        "profile",
                        "email",
                        "offline_access",
                        "urn:zitadel:iam:user:metadata",
                    ),
                )
            }
        }

        jwt(AuthMethods.JWT) {
            realm = jwtAuthTokenProperties.realm.value
            verifier(
                JWT.require(Algorithm.HMAC512(jwtAuthTokenProperties.secret.value))
                    .withAudience(jwtAuthTokenProperties.audience.value)
                    .withIssuer(jwtAuthTokenProperties.issuer.value)
                    .build(),
            )
            validate { credential ->
                val audience = credential.payload.audience
                if (audience.contains(jwtAuthTokenProperties.audience.value)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { scheme, realm ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ProblemHttpErrorResponse(
                        "unauthorized",
                        "Authorization failed",
                        call.request.uri,
                        "$scheme token to access $realm realm is either invalid or expired.",
                    ),
                )
            }
        }
    }
}
