package com.szastarek.gymz.adapter.cerbos

import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.BatchDecision
import com.szastarek.gymz.domain.service.user.Decision
import com.szastarek.gymz.shared.security.UserContext
import dev.cerbos.sdk.CerbosBlockingClient
import dev.cerbos.sdk.builders.Principal
import dev.cerbos.sdk.builders.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class CerbosAccessManager(
    private val cerbos: CerbosBlockingClient,
) : AccessManager {

    override fun check(userContext: UserContext, resource: Resource, action: Action): Decision = runBlocking(Dispatchers.IO) {
        cerbos.check(userContext.toPrincipal(), resource, action.value)
            .isAllowed(action.value)
            .toDecision(userContext.toPrincipal(), resource, action)
    }

    override fun checkAll(userContext: UserContext, resources: List<Resource>, action: Action): BatchDecision = runBlocking(Dispatchers.IO) {
        val allChecksPassed = cerbos.batch(userContext.toPrincipal())
            .apply { resources.forEach { this.addResourceAndActions(it, action.value) } }
            .check().results().toList()
            .all { it.isAllowed(action.value) }

        when (allChecksPassed) {
            true -> BatchDecision.Allow(userContext.toPrincipal(), resources, action)
            false -> BatchDecision.Deny(userContext.toPrincipal(), resources, action)
        }
    }
}

fun UserContext.toPrincipal(): Principal =
    Principal.newInstance(
        userId.value,
        *roles.map { it.code() }.toTypedArray(),
    )

private fun Boolean.toDecision(principal: Principal, resource: Resource, action: Action): Decision =
    when (this) {
        true -> Decision.Allow(principal, resource, action)
        false -> Decision.Deny(principal, resource, action)
    }
