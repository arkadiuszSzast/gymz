package com.szastarek.gymz.adapter.rest.response

import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.FamilyName
import com.szastarek.gymz.shared.model.GivenName
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.security.UserId
import kotlinx.serialization.Serializable
import pl.brightinventions.codified.enums.CodifiedEnum

typealias CodifiedRole =
    @Serializable(
        with = Role.CodifiedSerializer::class,
    )
    CodifiedEnum<Role, String>

@Serializable
data class UserInfoResponse(
    val id: UserId,
    val email: EmailAddress,
    val givenName: GivenName,
    val familyName: FamilyName,
    val roles: List<CodifiedRole>,
)
