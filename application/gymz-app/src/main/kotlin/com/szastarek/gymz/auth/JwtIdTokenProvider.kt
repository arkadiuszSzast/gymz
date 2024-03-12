package com.szastarek.gymz.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.szastarek.gymz.config.JwtIdTokenProperties
import com.szastarek.gymz.shared.security.Jwt
import com.szastarek.gymz.shared.security.Jwt.Companion.signCatching

class JwtIdTokenProvider(private val jwtIdTokenProperties: JwtIdTokenProperties) {
    private val allowedClaims =
        listOf(
            "email",
            "family_name",
            "gender",
            "given_name",
            "locale",
            "name",
            "preferred_username",
            "sub",
        )

    fun provide(externalJwtToken: Jwt) =
        JWT.create()
            .withAudience(jwtIdTokenProperties.audience.value)
            .withIssuer(jwtIdTokenProperties.issuer.value)
            .withExpiresAt(externalJwtToken.decoded.expiresAtAsInstant)
            .apply {
                externalJwtToken.decoded.claims
                    .filter { it.key in allowedClaims }
                    .forEach { (key, value) -> withClaim(key, value.asString()) }
            }
            .appendRoles(externalJwtToken)
            .appendMetadata(externalJwtToken)
            .signCatching(Algorithm.HMAC512(jwtIdTokenProperties.secret.value))
}

fun extractRoles(externalJwtToken: Jwt) =
    externalJwtToken.decoded.claims["urn:zitadel:iam:org:project:roles"]?.asMap()?.keys.orEmpty()

fun extractMetadata(externalJwtToken: Jwt) =
    externalJwtToken.decoded.claims["urn:zitadel:iam:user:metadata"]
        ?.asMap()
        ?.mapValues { it.value.toString() }
        .orEmpty()

private fun JWTCreator.Builder.appendRoles(externalJwtToken: Jwt): JWTCreator.Builder =
    withArrayClaim("roles", extractRoles(externalJwtToken).toTypedArray())

private fun JWTCreator.Builder.appendMetadata(externalJwtToken: Jwt): JWTCreator.Builder =
    withClaim("metadata", extractMetadata(externalJwtToken))
