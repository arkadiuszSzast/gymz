package com.szastarek.gymz.adapter.eventstore.user.equipment

import arrow.core.Option
import arrow.core.toNonEmptyListOrNone
import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipments
import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipmentsBuilder
import com.szastarek.gymz.domain.model.user.equipment.UserOwnedEquipmentsEvent
import com.szastarek.gymz.domain.service.user.equipment.UserOwnedEquipmentsRepository
import com.szastarek.gymz.event.store.model.getOrThrow
import com.szastarek.gymz.event.store.service.EventStoreReadClient
import com.szastarek.gymz.event.store.service.readStream
import com.szastarek.gymz.shared.security.UserId

class EventStoreUserOwnedEquipmentsRepository(
    private val eventStoreReadClient: EventStoreReadClient,
) : UserOwnedEquipmentsRepository {
    override suspend fun find(userId: UserId): Option<UserOwnedEquipments> {
        return eventStoreReadClient.readStream<UserOwnedEquipmentsEvent>(
            UserOwnedEquipmentsEvent.aggregateStreamName(userId),
        )
            .toNonEmptyListOrNone()
            .map { UserOwnedEquipmentsBuilder().apply(it).getOrThrow() }
    }
}
