package com.szastarek.gymz.adapter.cerbos

import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.Decision
import com.szastarek.gymz.shared.security.UserContext
import dev.cerbos.sdk.CerbosBlockingClient
import dev.cerbos.sdk.builders.Principal
import dev.cerbos.sdk.builders.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class CerbosClient(
    private val cerbos: CerbosBlockingClient,
) : AccessManager {

    override fun check(userContext: UserContext, resource: Resource, action: Action): Decision = runBlocking(Dispatchers.IO) {
        cerbos.check(userContext.toPrincipal(), resource, action.value)
            .isAllowed(action.value)
            .toDecision()
    }
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
