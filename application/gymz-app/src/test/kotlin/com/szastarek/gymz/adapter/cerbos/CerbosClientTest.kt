package com.szastarek.gymz.adapter.cerbos

import com.szastarek.gymz.fixtures.emailAddress
import com.szastarek.gymz.shared.model.EmailAddress
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.security.UserContext
import com.szastarek.gymz.shared.security.UserId
import com.szastarek.gymz.shared.validation.getOrThrow
import com.szastarek.gymz.user.Action
import com.szastarek.gymz.user.Decision
import dev.cerbos.sdk.CerbosClientBuilder
import dev.cerbos.sdk.CerbosContainer
import dev.cerbos.sdk.builders.AttributeValue.stringValue
import dev.cerbos.sdk.builders.Resource
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.uuid
import io.kotest.property.checkAll
import org.testcontainers.containers.BindMode
import org.testcontainers.images.RemoteDockerImage
import org.testcontainers.utility.DockerImageName
import pl.brightinventions.codified.enums.codifiedEnum

class CerbosClientTest : StringSpec({

    PropertyTesting.defaultIterationCount = 5

    val cerbos = CerbosContainer()
        .withClasspathResourceMapping("policies", "/policies", BindMode.READ_ONLY)
        .apply {
            image = RemoteDockerImage(DockerImageName.parse("docker.io/cerbos/cerbos:0.34.0"))
            start()
        }
    val cerbosClient = CerbosClient(CerbosClientBuilder(cerbos.target).withPlaintext().buildBlockingClient())

    afterSpec {
        cerbos.stop()
    }

    "should allow for edit action when user is owner" {
        checkAll(
            Arb.uuid().map { UserId(it.toString()).getOrThrow() },
            Arb.emailAddress,
            Arb.uuid().map { it.toString() },
        ) { userId, email, albumId ->
            // arrange
            val userContext = object : UserContext {
                override val userId = userId
                override val email: EmailAddress = email
                override val roles = listOf(Role.User.codifiedEnum())
            }
            val resource = Resource.newInstance("album:object", albumId)
                .withAttribute("ownerId", stringValue(userContext.userId.value))

            // act && assert
            cerbosClient.check(userContext, resource, Action("edit")) shouldBe Decision.Allow
        }
    }

    "should deny for edit action when user is not owner" {
        checkAll(
            Arb.uuid().map { UserId(it.toString()).getOrThrow() },
            Arb.emailAddress,
            Arb.uuid().map { it.toString() },
        ) { userId, email, albumId ->
            // arrange
            val userContext = object : UserContext {
                override val userId = userId
                override val email: EmailAddress = email
                override val roles = listOf(Role.User.codifiedEnum())
            }
            val resource = Resource.newInstance("album:object", albumId)
                .withAttribute("ownerId", stringValue("another-user-id"))

            // act && assert
            cerbosClient.check(userContext, resource, Action("edit")) shouldBe Decision.Deny
        }
    }
})
