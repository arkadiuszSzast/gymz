package com.szastarek.gymz.auth

import arrow.core.Either
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.szastarek.gymz.config.JwtAuthTokenProperties
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.security.AccessToken
import com.szastarek.gymz.shared.security.Jwt
import com.szastarek.gymz.shared.security.Jwt.Companion.signCatching
import com.szastarek.gymz.shared.security.JwtCreationError
import com.szastarek.gymz.shared.security.JwtSubject
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import pl.brightinventions.codified.enums.CodifiedEnum
import kotlin.time.Duration

class JwtAuthTokenProvider(
    private val clock: Clock,
    private val jwtAuthTokenProperties: JwtAuthTokenProperties,
) {
    fun provide(
        accessToken: AccessToken,
        subject: JwtSubject,
        roles: List<CodifiedEnum<Role, String>>,
        expiresIn: Duration,
    ): Either<JwtCreationError, Jwt> =
        JWT.create()
            .withSubject(subject.value)
            .withAudience(jwtAuthTokenProperties.audience.value)
            .withIssuer(jwtAuthTokenProperties.issuer.value)
            .withClaim("access_token", accessToken.value)
            .withArrayClaim("roles", roles.map { it.code() }.toTypedArray())
            .withExpiresAt(clock.now().plus(expiresIn).toJavaInstant())
            .signCatching(Algorithm.HMAC512(jwtAuthTokenProperties.secret.value))
}
