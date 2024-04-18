package com.szastarek.gymz.support

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.FamilyName
import com.szastarek.gymz.shared.model.GivenName
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.security.Jwt
import com.szastarek.gymz.shared.security.emailAddress
import com.szastarek.gymz.shared.security.familyName
import com.szastarek.gymz.shared.security.givenName
import com.szastarek.gymz.shared.security.role
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.uuid
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import pl.brightinventions.codified.enums.codifiedEnum
import kotlin.time.Duration.Companion.minutes

fun createZitadelIdToken(
    subject: String = Arb.uuid().next().toString(),
    email: EmailAddress = Arb.emailAddress.next(),
    emailVerified: Boolean = true,
    givenName: GivenName = Arb.givenName.next(),
    familyName: FamilyName = Arb.familyName.next(),
    roles: List<Role> = listOf(Arb.role.next()),
    expiresAt: Instant = Clock.System.now().plus(10.minutes),
    metadata: Map<String, String> = mapOf("meta_key" to "meta_value"),
): Jwt = JWT.create()
    .withSubject(subject)
    .withClaim("email", email.value)
    .withClaim("email_verified", emailVerified)
    .withClaim("given_name", givenName.value)
    .withClaim("family_name", familyName.value)
    .withClaim("urn:zitadel:iam:org:project:roles", roles.map { it.codifiedEnum() }.associate { it.code() to it.code() })
    .withClaim("urn:zitadel:iam:user:metadata", metadata)
    .withExpiresAt(expiresAt.toJavaInstant())
    .sign(Algorithm.HMAC512("test_auth"))
    .let { Jwt.fromRawString(it).getOrNull()!! }
