package com.szastarek.gymz.domain.service.user

import com.szastarek.gymz.shared.acl.AclResource
import com.szastarek.gymz.shared.security.UnauthorizedException
import com.szastarek.gymz.shared.security.UserContext
import dev.cerbos.sdk.builders.Principal
import dev.cerbos.sdk.builders.Resource

interface AccessManager {
    fun check(userContext: UserContext, resource: Resource, action: Action): Decision
}

fun AccessManager.check(userContext: UserContext, aclResource: AclResource, action: Action) =
    check(userContext, aclResource.resource, action)

@JvmInline
value class Action(val value: String) {
    companion object {
        val create = Action("Create")
        val read = Action("Read")
        val update = Action("Update")
        val delete = Action("Delete")
        val upload = Action("Upload")
    }
}

sealed interface Decision {
    val principal: Principal
    val resource: Resource
    val action: Action

    data class Allow(override val principal: Principal, override val resource: Resource, override val action: Action) :
        Decision

    data class Deny(override val principal: Principal, override val resource: Resource, override val action: Action) :
        Decision
}

fun Decision.ensure() {
    if (this is Decision.Deny) {
        throw UnauthorizedException("Principal ${principal.toPrincipal().id} is not allowed to perform action ${action.value} on resource ${resource.toResource().kind}")
    }
}
