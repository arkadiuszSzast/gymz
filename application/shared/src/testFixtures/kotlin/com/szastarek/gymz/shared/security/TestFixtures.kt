package com.szastarek.gymz.shared.security

import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.Role
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import pl.brightinventions.codified.enums.codifiedEnum

object TestFixtures {

    fun userContext(
        userId: UserId = Arb.Companion.userId.next(),
        email: EmailAddress = Arb.Companion.emailAddress.next(),
        roles: List<Role> = listOf(Arb.Companion.role.next()),
    ) = SimpleUserContext(
        userId = userId,
        email = email,
        roles = roles.map { it.codifiedEnum() },
    )
}
