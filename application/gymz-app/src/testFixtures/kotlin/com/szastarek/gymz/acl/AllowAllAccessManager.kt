package com.szastarek.gymz.acl

import com.szastarek.gymz.adapter.cerbos.toPrincipal
import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.Decision
import com.szastarek.gymz.shared.security.UserContext
import dev.cerbos.sdk.builders.Resource

object AllowAllAccessManager : AccessManager {
    override fun check(userContext: UserContext, resource: Resource, action: Action): Decision =
        Decision.Allow(userContext.toPrincipal(), resource, action)
}
