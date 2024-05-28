package com.szastarek.gymz.shared.security

import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.validation.getOrThrow
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import pl.brightinventions.codified.enums.CodifiedEnum

interface UserContext {
    val userId: UserId
    val email: EmailAddress
    val roles: List<CodifiedEnum<Role, String>>
}

data class SimpleUserContext(
    override val userId: UserId,
    override val email: EmailAddress,
    override val roles: List<CodifiedEnum<Role, String>>,
) : UserContext

val ApplicationCall.userContext: UserContext
    get() {
        val principal = principal<JWTPrincipal>() ?: throw UnauthorizedException("Principal not found")
        return SimpleUserContext(
            userId = principal.subject?.let { UserId(it).getOrThrow() } ?: throw UnauthorizedException("Claim userId is missing"),
            email = principal["email"]?.let { EmailAddress(it).getOrThrow() } ?: throw UnauthorizedException("Claim email is missing"),
            roles = principal.getListClaim("roles", String::class).map { CodifiedEnum.decode<Role>(it) },
        )
    }
