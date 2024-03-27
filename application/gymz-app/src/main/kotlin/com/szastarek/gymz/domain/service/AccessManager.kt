package com.szastarek.gymz.domain.service

import com.szastarek.gymz.shared.security.UserContext
import dev.cerbos.sdk.builders.Resource

interface AccessManager {
    fun check(userContext: UserContext, resource: Resource, action: Action): Decision
}

@JvmInline
value class Action(val value: String)

sealed interface Decision {
    data object Allow : Decision
    data object Deny : Decision
}
