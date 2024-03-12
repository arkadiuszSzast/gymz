package com.szastarek.gymz.query.handler

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.raise.zipOrAccumulate
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.szastarek.gymz.config.JwtIdTokenProperties
import com.szastarek.gymz.query.UserInfoFoundResult
import com.szastarek.gymz.query.UserInfoQuery
import com.szastarek.gymz.query.UserInfoQueryError
import com.szastarek.gymz.query.UserInfoQueryResult
import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.FamilyName
import com.szastarek.gymz.shared.model.GivenName
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.user.UserId
import com.szastarek.gymz.user.UserInfo
import com.trendyol.kediatr.QueryHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import pl.brightinventions.codified.enums.codifiedEnum

private val logger = KotlinLogging.logger {}

internal class UserInfoQueryHandler(
    private val jwtIdTokenProperties: JwtIdTokenProperties,
) : QueryHandler<UserInfoQuery, UserInfoQueryResult> {
    override suspend fun handle(query: UserInfoQuery): UserInfoQueryResult = either {
        val token = query.token
        val secret = jwtIdTokenProperties.secret
        val decodedToken = token.verify(Algorithm.HMAC512(secret.value))
            .mapLeft {
                logger.error { "Token secret did not validate: $it" }
                UserInfoQueryError.InvalidJwt.nel()
            }
            .bind()
        val roles = decodedToken.claims["roles"]?.asList(String::class.java).orEmpty()
            .map { it.codifiedEnum<Role>() }

        zipOrAccumulate(
            {
                UserId(decodedToken.subjectOrEmpty).mapLeft {
                    logger.error { "Failed to extract subject from id token because of: $it" }
                    UserInfoQueryError.InvalidJwt
                }.bind()
            },
            {
                EmailAddress(decodedToken.claims["email"]?.asString().orEmpty()).mapLeft {
                    logger.error { "Failed to extract email from id token because of: $it" }
                    UserInfoQueryError.InvalidJwt
                }.bind()
            },
            {
                ensureNotNull(decodedToken.claims["given_name"]) {
                    logger.error { "Token is missing given_name claim" }
                    UserInfoQueryError.InvalidJwt
                }.asString().let { GivenName(it) }
            },
            {
                ensureNotNull(decodedToken.claims["family_name"]) {
                    logger.error { "Token is missing family_name claim" }
                    UserInfoQueryError.InvalidJwt
                }.asString().let { FamilyName(it) }
            },
            { userId, email, givenName, familyName ->
                UserInfoFoundResult(
                    UserInfo(
                        id = userId,
                        email = email,
                        givenName = givenName,
                        familyName = familyName,
                        roles = roles,
                    ),
                )
            },
        )
    }
}

private val DecodedJWT.subjectOrEmpty: String
    get() = try {
        subject
    } catch (e: NullPointerException) {
        ""
    }
