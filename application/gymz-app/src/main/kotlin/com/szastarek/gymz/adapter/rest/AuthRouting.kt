package com.szastarek.gymz.adapter.rest

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.toOption
import com.szastarek.gymz.adapter.rest.response.LoginResponse
import com.szastarek.gymz.adapter.rest.response.UserInfoResponse
import com.szastarek.gymz.domain.service.user.query.UserInfoQuery
import com.szastarek.gymz.service.auth.JwtAuthTokenProvider
import com.szastarek.gymz.service.auth.JwtIdTokenProvider
import com.szastarek.gymz.service.plugins.jwtAuthenticate
import com.szastarek.gymz.service.plugins.oauthAuthenticate
import com.szastarek.gymz.shared.http.ProblemHttpErrorResponse
import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.security.AccessToken
import com.szastarek.gymz.shared.security.BearerToken
import com.szastarek.gymz.shared.security.Jwt
import com.szastarek.gymz.shared.security.JwtSubject
import com.szastarek.gymz.shared.validation.ValidationError
import com.szastarek.gymz.shared.validation.getOrThrow
import com.trendyol.kediatr.Mediator
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.principal
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import pl.brightinventions.codified.enums.codifiedEnum
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

const val AUTH_API_PREFIX = "/auth"

enum class AuthenticateError(val message: String) {
    NoPrincipal("Principal not found"),
    NoIdToken("Id token not found"),
    MissingClaim("Token is missing claim"),
    NoRefreshToken("Refresh token not found"),
    FailedToCreateJwtToken("Failed to create JWT token"),
}

fun Application.configureRouting(
    jwtAuthTokenProvider: JwtAuthTokenProvider,
    jwtIdTokenProvider: JwtIdTokenProvider,
) {
    val mediator by inject<Mediator>()

    routing {
        oauthAuthenticate {
            get("/$AUTH_API_PREFIX/login") {
                // Automatically redirects to `authorizeUrl`
            }
            get("/$AUTH_API_PREFIX/callback") {
                either {
                    val principal = call.principal<OAuthAccessTokenResponse.OAuth2>()
                    ensureNotNull(principal) { AuthenticateError.NoPrincipal }

                    val jwtIdToken = getIdJwtToken(principal, jwtIdTokenProvider)
                    val roles = jwtIdToken.decoded.getClaim("roles").asArray(String::class.java).orEmpty()
                        .map { it.codifiedEnum<Role>() }
                    val email = catch({ EmailAddress(jwtIdToken.decoded.getClaim("email").asString()).getOrThrow() }) {
                        raise(AuthenticateError.MissingClaim)
                    }

                    val refreshToken = principal.refreshToken
                    ensureNotNull(refreshToken) { AuthenticateError.NoRefreshToken }

                    val subject = JwtSubject(jwtIdToken.decoded.subject)
                    val accessToken = AccessToken(principal.accessToken)

                    val authToken =
                        jwtAuthTokenProvider.provide(accessToken, subject, email, roles, principal.expiresIn.seconds).mapLeft {
                            logger.error { "Failed to create auth JWT token because of: ${it.details}" }
                            AuthenticateError.FailedToCreateJwtToken
                        }.bind()

                    LoginResponse(authToken, jwtIdToken, BearerToken(refreshToken))
                }
                    .fold(
                        { error ->
                            call.respond(
                                HttpStatusCode.Unauthorized,
                                ProblemHttpErrorResponse(
                                    "unauthorized",
                                    "Authorization failed",
                                    call.request.uri,
                                    error.message,
                                ),
                            )
                        },
                        { response -> call.respond(response) },
                    )
            }
        }

        jwtAuthenticate {
            get("/$AUTH_API_PREFIX/me") {
                val idToken = call.request.queryParameters["id_token"].toOption().toEither {
                    ValidationError(".id_token", "id_token is required")
                }.getOrThrow()

                val query = UserInfoQuery(idToken).getOrThrow()

                val result = mediator.send(query)

                result.fold(
                    {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ProblemHttpErrorResponse(
                                "bad_request",
                                "Bad request ",
                                call.request.uri,
                                "Failed to get user info",
                            ),
                        )
                    },
                    {
                        val userInfo = it.userInfo
                        call.respond(
                            UserInfoResponse(
                                userInfo.id,
                                userInfo.email,
                                userInfo.givenName,
                                userInfo.familyName,
                                userInfo.roles,
                            ),
                        )
                    },
                )
            }
        }
    }
}

private fun Raise<AuthenticateError>.getIdJwtToken(
    principal: OAuthAccessTokenResponse.OAuth2,
    jwtIdTokenProvider: JwtIdTokenProvider,
): Jwt {
    val externalToken = ensureNotNull(principal.extraParameters["id_token"]) { AuthenticateError.NoIdToken }
    val externalIdToken = Jwt.fromRawString(externalToken).mapLeft {
        logger.error { "Failed to create id JWT token because of: ${it.details}" }
        AuthenticateError.FailedToCreateJwtToken
    }.bind()
    return jwtIdTokenProvider.provide(externalIdToken).mapLeft {
        logger.error { "Failed to rewrite external id JWT token because of: ${it.details}" }
        AuthenticateError.FailedToCreateJwtToken
    }.bind()
}
