package com.szastarek.gymz.adapter.cerbos

import com.szastarek.gymz.shared.security.UserContext
import com.szastarek.gymz.user.AccessManager
import com.szastarek.gymz.user.Action
import com.szastarek.gymz.user.Decision
import dev.cerbos.sdk.CerbosBlockingClient
import dev.cerbos.sdk.builders.Principal
import dev.cerbos.sdk.builders.Resource

class CerbosClient(
    private val cerbos: CerbosBlockingClient,
) : AccessManager {

    override fun check(userContext: UserContext, resource: Resource, action: Action): Decision =
        cerbos.check(userContext.toPrincipal(), resource, action.value)
            .isAllowed(action.value)
            .toDecision()
}

private fun Boolean.toDecision(): Decision =
    when (this) {
        true -> Decision.Allow
        false -> Decision.Deny
    }

private fun UserContext.toPrincipal(): Principal =
    Principal.newInstance(
        userId.value,
        *roles.map { it.code() }.toTypedArray(),
    )