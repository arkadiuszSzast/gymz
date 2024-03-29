package com.szastarek.gymz.support

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.szastarek.gymz.adapter.rest.response.LoginResponse
import com.szastarek.gymz.module
import com.szastarek.gymz.service.auth.JwtAuthTokenProvider
import com.szastarek.gymz.service.auth.JwtIdTokenProvider
import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.FamilyName
import com.szastarek.gymz.shared.model.GivenName
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.security.AccessToken
import com.szastarek.gymz.shared.security.BearerToken
import com.szastarek.gymz.shared.security.Jwt
import com.szastarek.gymz.shared.security.JwtSubject
import com.szastarek.gymz.shared.security.UserId
import com.szastarek.gymz.shared.validation.getOrThrow
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.scopes.StringSpecScope
import io.kotest.core.spec.style.scopes.addTest
import io.kotest.property.Arb
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.arbs.firstName
import io.kotest.property.arbs.lastName
import io.ktor.client.HttpClient
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import pl.brightinventions.codified.enums.codifiedEnum
import java.util.UUID
import kotlin.time.Duration.Companion.minutes
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation.Plugin as ClientContentNegotiation

abstract class IntegrationTest : StringSpec(), KoinTest {
    private val authTokenProvider: JwtAuthTokenProvider by lazy { JwtAuthTokenProvider(get(), get()) }
    private val idTokenProvider: JwtIdTokenProvider by lazy { JwtIdTokenProvider(get()) }

    operator fun String.invoke(test: suspend StringSpecScope.(client: HttpClient) -> Unit) {
        addTest(TestName(null, this, false), false, null) {
            StringSpecScope(this.coroutineContext, testCase).withClient(test)
        }
    }

    fun authenticate(
        userId: UserId = UserId(UUID.randomUUID().toString()).getOrThrow(),
        email: EmailAddress = EmailAddress(Arb.email().next()).getOrThrow(),
        roles: List<Role> = Arb.list(Arb.enum<Role>(), 1..1).next(),
        givenName: GivenName = GivenName(Arb.firstName().next().name),
        familyName: FamilyName = FamilyName(Arb.lastName().next().name),
    ): LoginResponse {
        val codifiedRoles = roles.map { it.codifiedEnum() }
        val authToken = authTokenProvider.provide(
            AccessToken(UUID.randomUUID().toString()),
            JwtSubject(userId.value),
            email,
            codifiedRoles,
            10.minutes,
        ).getOrNull()!!
        val idToken = JWT.create()
            .withSubject(userId.value)
            .withExpiresAt(Clock.System.now().plus(10.minutes).toJavaInstant())
            .withClaim("email", email.value)
            .withClaim("given_name", givenName.value)
            .withClaim("family_name", familyName.value)
            .withClaim("urn:zitadel:iam:org:project:roles", codifiedRoles.associate { it.code() to it.code() })
            .sign(Algorithm.HMAC512("test_auth"))
            .let { Jwt.fromRawString(it).getOrNull()!! }
            .let { idTokenProvider.provide(it).getOrNull()!! }

        return LoginResponse(authToken, idToken, BearerToken(UUID.randomUUID().toString()))
    }

    private suspend fun StringSpecScope.withClient(test: suspend StringSpecScope.(client: HttpClient) -> Unit) {
        testApplication {
            application {
                module(this@testApplication.client)
            }
            val client = createClient {
                expectSuccess = false
                install(ClientContentNegotiation) {
                    json(Json)
                }
            }
            startApplication()
            test(client)
            stopKoin()
        }
    }
}
