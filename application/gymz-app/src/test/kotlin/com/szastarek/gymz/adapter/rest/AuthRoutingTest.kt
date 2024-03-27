package com.szastarek.gymz.adapter.rest

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.szastarek.gymz.adapter.rest.response.UserInfoResponse
import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.FamilyName
import com.szastarek.gymz.shared.model.GivenName
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.security.UserId
import com.szastarek.gymz.shared.validation.getOrThrow
import com.szastarek.gymz.support.IntegrationTest
import com.szastarek.gymz.support.me
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.arbs.firstName
import io.kotest.property.arbs.lastName
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import pl.brightinventions.codified.enums.codifiedEnum
import java.util.UUID

class AuthRoutingTest : IntegrationTest() {

    init {

        "should return 401 when trying to access /me without access token" { client ->
            // arrange
            val loginResponse = authenticate()

            // act && assert
            client.get("$AUTH_API_PREFIX/me") {
                parameter("id_token", loginResponse.idToken.value)
            }.status shouldBe HttpStatusCode.Unauthorized
        }

        "should return 400 when id_token not provided" { client ->
            // arrange
            val loginResponse = authenticate()

            // act && assert
            client.get("$AUTH_API_PREFIX/me") {
                bearerAuth(loginResponse.authToken.value)
            }.status shouldBe HttpStatusCode.BadRequest
        }

        "should return 400 when id_token is invalid" { client ->
            // arrange
            val loginResponse = authenticate()
            val invalidIdToken = JWT.create()
                .sign(Algorithm.HMAC512("invalid-secret"))

            // act && assert
            client.get("$AUTH_API_PREFIX/me") {
                bearerAuth(loginResponse.authToken.value)
                parameter("id_token", invalidIdToken)
            }.status shouldBe HttpStatusCode.BadRequest
        }

        "should return user info" { client ->
            // arrange
            val userId = UserId(UUID.randomUUID().toString()).getOrThrow()
            val email = EmailAddress(Arb.email().next()).getOrThrow()
            val roles = Arb.list(Arb.enum<Role>(), 1..1).next()
            val givenName = GivenName(Arb.firstName().next().name)
            val familyName = FamilyName(Arb.lastName().next().name)

            val loginResponse = authenticate(userId, email, roles, givenName, familyName)

            // act && assert
            val response = client.me(loginResponse.authToken, loginResponse.idToken)
            response.status shouldBe HttpStatusCode.OK
            response.body<UserInfoResponse>() shouldBe UserInfoResponse(
                userId,
                email,
                givenName,
                familyName,
                roles.map { it.codifiedEnum() },
            )
        }
    }
}
