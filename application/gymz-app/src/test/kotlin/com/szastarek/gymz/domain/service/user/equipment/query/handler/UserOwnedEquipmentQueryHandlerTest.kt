package com.szastarek.gymz.domain.service.user.equipment.query.handler

import com.szastarek.gymz.TestFixtures.userOwnedEquipmentsEventChanged
import com.szastarek.gymz.acl.AllowAllAccessManager
import com.szastarek.gymz.adapter.eventstore.user.equipment.EventStoreUserOwnedEquipmentsRepository
import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipments
import com.szastarek.gymz.domain.service.user.equipment.query.UserOwnedEquipmentQuery
import com.szastarek.gymz.event.store.InMemoryEventStore
import com.szastarek.gymz.event.store.model.ExpectedRevision
import com.szastarek.gymz.event.store.service.appendToStream
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import com.szastarek.gymz.shared.security.TestFixtures.userContext as userContext

class UserOwnedEquipmentQueryHandlerTest : StringSpec({

    val eventStore = InMemoryEventStore()
    val handler = UserOwnedEquipmentQueryHandler(AllowAllAccessManager, EventStoreUserOwnedEquipmentsRepository(eventStore))

    beforeTest {
        eventStore.clear()
    }

    "should return empty list when user has no equipment" {
        // arrange
        val userContext = userContext()

        // act
        val equipments = handler.handle(UserOwnedEquipmentQuery(userContext))

        // assert
        equipments shouldBe UserOwnedEquipments(userContext.userId, emptyList())
    }

    "should return user owned equipment" {
        // arrange
        val userContext = userContext()
        val event = userOwnedEquipmentsEventChanged(userContext)
        eventStore.appendToStream(event, ExpectedRevision.Any)

        // act
        val equipments = handler.handle(UserOwnedEquipmentQuery(userContext))

        // assert
        equipments shouldBe UserOwnedEquipments(userContext.userId, event.equipments)
    }
})
