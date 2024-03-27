package com.szastarek.gymz.user

import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.FamilyName
import com.szastarek.gymz.shared.model.GivenName
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.security.UserId
import pl.brightinventions.codified.enums.CodifiedEnum

data class UserInfo(
    val id: UserId,
    val email: EmailAddress,
    val givenName: GivenName,
    val familyName: FamilyName,
    val roles: List<CodifiedEnum<Role, String>>,
)
