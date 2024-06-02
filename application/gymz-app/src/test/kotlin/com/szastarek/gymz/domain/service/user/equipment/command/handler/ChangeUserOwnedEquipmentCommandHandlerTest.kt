package com.szastarek.gymz.domain.service.user.equipment.command.handler

import arrow.core.nel
import com.szastarek.gymz.acl.AllowAllAccessManager
import com.szastarek.gymz.adapter.eventstore.user.equipment.EventStoreUserOwnedEquipmentsRepository
import com.szastarek.gymz.domain.model.equipment.EquipmentId
import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipmentsEvent
import com.szastarek.gymz.domain.service.user.equipment.command.ChangeUserOwnedEquipmentCommand
import com.szastarek.gymz.domain.service.user.equipment.command.ChangeUserOwnedEquipmentCommandError
import com.szastarek.gymz.domain.service.user.equipment.command.ChangeUserOwnedEquipmentCommandSuccessResult
import com.szastarek.gymz.event.store.InMemoryEventStore
import com.szastarek.gymz.event.store.service.readStream
import com.szastarek.gymz.fixtures.EquipmentTestFixtures.supportedEquipments
import com.szastarek.gymz.fixtures.EquipmentTestFixtures.userOwnedEquipmentsEventChanged
import com.szastarek.gymz.shared.model.Role
import com.szastarek.gymz.shared.security.TestFixtures.userContext
import com.szastarek.gymz.support.InMemorySupportedEquipmentRepository
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forSingle
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields

class ChangeUserOwnedEquipmentCommandHandlerTest : StringSpec({

    val eventStore = InMemoryEventStore()
    val supportedEquipmentRepository = InMemorySupportedEquipmentRepository()
    val handler = ChangeUserOwnedEquipmentCommandHandler(
        AllowAllAccessManager,
        EventStoreUserOwnedEquipmentsRepository(eventStore),
        supportedEquipmentRepository,
        eventStore,
    )

    beforeTest {
        eventStore.clear()
        supportedEquipmentRepository.clear()
    }

    "should create user owned equipment" {
        // arrange
        val userContext = userContext(roles = listOf(Role.User))
        val supportedEquipments = supportedEquipments()
        supportedEquipmentRepository.addAll(supportedEquipments.equipments)

        val ownedEquipments = supportedEquipments.equipments
            .take(supportedEquipments.equipments.size - 1)
        val ownedEquipmentsIds = ownedEquipments
            .map { it.id }

        // act
        val result = handler.handle(ChangeUserOwnedEquipmentCommand(userContext, ownedEquipmentsIds))

        // assert
        result shouldBeRight ChangeUserOwnedEquipmentCommandSuccessResult
        val events = eventStore.readStream<UserOwnedEquipmentsEvent>(UserOwnedEquipmentsEvent.aggregateStreamName(userContext.userId))
        events.forSingle {
            it.shouldBeEqualToIgnoringFields(userOwnedEquipmentsEventChanged(userContext, ownedEquipments), UserOwnedEquipmentsEvent::metadata)
        }
    }

    "should return error when equipment not found" {
        // arrange
        val userContext = userContext(roles = listOf(Role.User))

        val ownedEquipmentsIds = listOf(EquipmentId("not-found"))

        // act
        val result = handler.handle(ChangeUserOwnedEquipmentCommand(userContext, ownedEquipmentsIds))

        // assert
        result shouldBeLeft ChangeUserOwnedEquipmentCommandError.EquipmentNotFound.nel()
        val events = eventStore.readStream<UserOwnedEquipmentsEvent>(UserOwnedEquipmentsEvent.aggregateStreamName(userContext.userId))
        events.shouldBeEmpty()
    }
})
