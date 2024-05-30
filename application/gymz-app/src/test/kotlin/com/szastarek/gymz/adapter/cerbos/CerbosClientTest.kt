package com.szastarek.gymz.adapter.cerbos

import com.szastarek.gymz.cerbos.CerbosContainer
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.Decision
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.security.SimpleUserContext
import com.szastarek.gymz.shared.security.UserId
import com.szastarek.gymz.shared.security.emailAddress
import com.szastarek.gymz.shared.validation.getOrThrow
import dev.cerbos.sdk.CerbosClientBuilder
import dev.cerbos.sdk.builders.AttributeValue.stringValue
import dev.cerbos.sdk.builders.Resource
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.uuid
import io.kotest.property.checkAll
import pl.brightinventions.codified.enums.codifiedEnum

class CerbosClientTest : StringSpec({

    PropertyTesting.defaultIterationCount = 5

    val cerbosAccessManager = CerbosAccessManager(CerbosClientBuilder(CerbosContainer.url).withPlaintext().buildBlockingClient())

    "should allow for edit action when user is owner" {
        checkAll(
            Arb.uuid().map { UserId(it.toString()).getOrThrow() },
            Arb.emailAddress,
            Arb.uuid().map { it.toString() },
        ) { userId, email, albumId ->
            // arrange
            val userContext = SimpleUserContext(
                userId = userId,
                email = email,
                roles = listOf(Role.User.codifiedEnum()),
            )
            val resource = Resource.newInstance("album:object", albumId)
                .withAttribute("ownerId", stringValue(userContext.userId.value))

            // act && assert
            cerbosAccessManager.check(userContext, resource, Action.update).shouldBeTypeOf<Decision.Allow>()
        }
    }

    "should deny for edit action when user is not owner" {
        checkAll(
            Arb.uuid().map { UserId(it.toString()).getOrThrow() },
            Arb.emailAddress,
            Arb.uuid().map { it.toString() },
        ) { userId, email, albumId ->
            // arrange
            val userContext = SimpleUserContext(
                userId = userId,
                email = email,
                roles = listOf(Role.User.codifiedEnum()),
            )
            val resource = Resource.newInstance("album:object", albumId)
                .withAttribute("ownerId", stringValue("another-user-id"))

            // act && assert
            cerbosAccessManager.check(userContext, resource, Action.update).shouldBeTypeOf<Decision.Deny>()
        }
    }
})
