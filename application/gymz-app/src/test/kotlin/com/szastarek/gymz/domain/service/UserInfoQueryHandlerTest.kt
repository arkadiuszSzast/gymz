package com.szastarek.gymz.domain.service

import arrow.core.nel
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.szastarek.gymz.config.JwtIdTokenProperties
import com.szastarek.gymz.domain.model.user.UserInfo
import com.szastarek.gymz.domain.service.query.UserInfoFoundResult
import com.szastarek.gymz.domain.service.query.UserInfoQuery
import com.szastarek.gymz.domain.service.query.UserInfoQueryError
import com.szastarek.gymz.domain.service.query.handler.UserInfoQueryHandler
import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.FamilyName
import com.szastarek.gymz.shared.model.GivenName
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.security.JwtAudience
import com.szastarek.gymz.shared.security.JwtIssuer
import com.szastarek.gymz.shared.security.JwtRealm
import com.szastarek.gymz.shared.security.MaskedString
import com.szastarek.gymz.shared.security.UserId
import com.szastarek.gymz.shared.validation.ValidationError
import com.szastarek.gymz.shared.validation.getOrThrow
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.uuid
import io.kotest.property.arbs.firstName
import io.kotest.property.arbs.lastName
import io.kotest.property.checkAll
import pl.brightinventions.codified.enums.codifiedEnum
import java.util.UUID

class UserInfoQueryHandlerTest : StringSpec({

    PropertyTesting.defaultIterationCount = 10

    val properties = JwtIdTokenProperties(
        audience = JwtAudience("test-audience"),
        issuer = JwtIssuer("test-issuer"),
        realm = JwtRealm("test-realm"),
        secret = MaskedString("test-secret"),
    )

    val handler = UserInfoQueryHandler(properties)

    "should get UserInfo" {
        checkAll(
            Arb.uuid(),
            Arb.enum<Role>(),
            Arb.email(),
            Arb.firstName(),
            Arb.lastName(),
        ) { subject, role, email, givenName, familyName ->
            // arrange
            val externalJwt = JWT.create()
                .withSubject(subject.toString())
                .withArrayClaim("roles", arrayOf(role.code))
                .withClaim("email", email)
                .withClaim("given_name", givenName.name)
                .withClaim("family_name", familyName.name)
                .sign(Algorithm.HMAC512(properties.secret.value))
            val query = UserInfoQuery(externalJwt).getOrThrow()

            // act
            val result = handler.handle(query)

            // assert
            result.shouldBeRight(
                UserInfoFoundResult(
                    UserInfo(
                        id = UserId(subject.toString()).getOrThrow(),
                        email = EmailAddress(email).getOrThrow(),
                        givenName = GivenName(givenName.name),
                        familyName = FamilyName(familyName.name),
                        roles = listOf(role.codifiedEnum()),
                    ),
                ),
            )
        }
    }

    "should not create query when invalid jwt is given" {
        // arrange && act
        val query = UserInfoQuery("invalid-jwt")

        // assert
        query.shouldBeLeft().shouldBeInstanceOf<ValidationError>()
    }

    "should return invalid jwt when signature is invalid" {
        // arrange
        val externalJwt = JWT.create()
            .withSubject(UUID.randomUUID().toString())
            .withArrayClaim("roles", arrayOf(Role.User.code))
            .withClaim("email", "test@test.com")
            .withClaim("given_name", "Joe")
            .withClaim("family_name", "Doe")
            .sign(Algorithm.HMAC512("invalid-secret"))
        val query = UserInfoQuery(externalJwt).getOrThrow()

        // act
        val result = handler.handle(query)

        // assert
        result.shouldBeLeft(UserInfoQueryError.InvalidJwt.nel())
    }

    "should return invalid jwt when subject is missing" {
        // arrange
        val externalJwt = JWT.create()
            .withArrayClaim("roles", arrayOf(Role.User.code))
            .withClaim("email", "test@test.com")
            .withClaim("given_name", "Joe")
            .withClaim("family_name", "Doe")
            .sign(Algorithm.HMAC512(properties.secret.value))
        val query = UserInfoQuery(externalJwt).getOrThrow()

        // act
        val result = handler.handle(query)

        // assert
        result.shouldBeLeft(UserInfoQueryError.InvalidJwt.nel())
    }

    "should return invalid jwt when email is missing" {
        // arrange
        val externalJwt = JWT.create()
            .withSubject(UUID.randomUUID().toString())
            .withArrayClaim("roles", arrayOf(Role.User.code))
            .withClaim("given_name", "Joe")
            .withClaim("family_name", "Doe")
            .sign(Algorithm.HMAC512(properties.secret.value))
        val query = UserInfoQuery(externalJwt).getOrThrow()

        // act
        val result = handler.handle(query)

        // assert
        result.shouldBeLeft(UserInfoQueryError.InvalidJwt.nel())
    }

    "should return invalid jwt when given_name is missing" {
        // arrange
        val externalJwt = JWT.create()
            .withSubject(UUID.randomUUID().toString())
            .withArrayClaim("roles", arrayOf(Role.User.code))
            .withClaim("email", "test@test.com")
            .withClaim("family_name", "Doe")
            .sign(Algorithm.HMAC512(properties.secret.value))
        val query = UserInfoQuery(externalJwt).getOrThrow()

        // act
        val result = handler.handle(query)

        // assert
        result.shouldBeLeft(UserInfoQueryError.InvalidJwt.nel())
    }

    "should return invalid jwt when family_name is missing" {
        // arrange
        val externalJwt = JWT.create()
            .withSubject(UUID.randomUUID().toString())
            .withArrayClaim("roles", arrayOf(Role.User.code))
            .withClaim("email", "test@test.com")
            .withClaim("given_name", "Joe")
            .sign(Algorithm.HMAC512(properties.secret.value))
        val query = UserInfoQuery(externalJwt).getOrThrow()

        // act
        val result = handler.handle(query)

        // assert
        result.shouldBeLeft(UserInfoQueryError.InvalidJwt.nel())
    }

    "should return empty roles when roles are missing" {
        // arrange
        val subject = UUID.randomUUID()
        val email = "test@test.com"
        val givenName = "Joe"
        val familyName = "Doe"
        val externalJwt = JWT.create()
            .withSubject(subject.toString())
            .withClaim("email", email)
            .withClaim("given_name", givenName)
            .withClaim("family_name", familyName)
            .sign(Algorithm.HMAC512(properties.secret.value))
        val query = UserInfoQuery(externalJwt).getOrThrow()

        // act
        val result = handler.handle(query)

        // assert
        result.shouldBeRight(
            UserInfoFoundResult(
                UserInfo(
                    id = UserId(subject.toString()).getOrThrow(),
                    email = EmailAddress(email).getOrThrow(),
                    givenName = GivenName(givenName),
                    familyName = FamilyName(familyName),
                    roles = emptyList(),
                ),
            ),
        )
    }
})
