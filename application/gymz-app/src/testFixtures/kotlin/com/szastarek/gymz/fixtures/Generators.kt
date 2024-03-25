package com.szastarek.gymz.fixtures

import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.FamilyName
import com.szastarek.gymz.shared.model.GivenName
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.validation.getOrThrow
import io.kotest.property.Arb
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.map
import io.kotest.property.arbs.firstName
import io.kotest.property.arbs.lastName

val Arb.Companion.givenName: Arb<GivenName>
    get() = Arb.firstName().map { GivenName(it.name) }

val Arb.Companion.familyName: Arb<FamilyName>
    get() = Arb.lastName().map { FamilyName(it.name) }

val Arb.Companion.emailAddress: Arb<EmailAddress>
    get() = Arb.email().map { EmailAddress(it).getOrThrow() }

val Arb.Companion.role: Arb<Role>
    get() = Arb.enum<Role>()
