package com.szastarek.gymz.shared.security

import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.Role
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import pl.brightinventions.codified.enums.CodifiedEnum
import pl.brightinventions.codified.enums.codifiedEnum

object TestFixtures {

    fun userContext(
        userId: UserId = Arb.Companion.userId.next(),
        email: EmailAddress = Arb.Companion.emailAddress.next(),
        roles: List<Role> = listOf(Arb.Companion.role.next()),
    ) = object : UserContext {
        override val userId: UserId
            get() = userId
        override val email: EmailAddress
            get() = email
        override val roles: List<CodifiedEnum<Role, String>>
            get() = roles.map { it.codifiedEnum() }
    }
}
