package com.szastarek.gymz.domain.model.user.equipment

import com.szastarek.gymz.domain.model.equipment.Equipment
import com.szastarek.gymz.event.store.model.AggregateId
import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventCategory
import com.szastarek.gymz.event.store.model.EventMetadata
import com.szastarek.gymz.event.store.model.EventMetadataBuilder
import com.szastarek.gymz.event.store.model.EventType
import com.szastarek.gymz.event.store.model.StreamName
import com.szastarek.gymz.shared.security.UserId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface UserOwnedEquipmentsEvent : DomainEvent<UserOwnedEquipmentsEvent> {

    companion object {
        val category = EventCategory("UserOwnedEquipments")

        fun aggregateStreamName(userId: UserId) = StreamName("${category.value}-${userId.value}")
    }

    @Serializable
    @SerialName("UserOwnedEquipmentsChanged")
    data class Changed(
        val userId: UserId,
        val equipments: List<Equipment>,
        override val metadata: EventMetadata,
    ) : UserOwnedEquipmentsEvent {

        companion object {
            val type = EventType(category, "Changed")

            fun metadata(userId: UserId, causedBy: EventMetadata? = null) = EventMetadataBuilder(
                AggregateId(userId.value),
                category,
                type,
            ).causedBy(causedBy).build()
        }

        override fun withMetadata(metadata: EventMetadata): Changed {
            return copy(metadata = metadata)
        }
    }
}
