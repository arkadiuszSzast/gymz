package com.szastarek.gymz.event.store

import com.szastarek.gymz.event.store.model.AggregateId
import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventCategory
import com.szastarek.gymz.event.store.model.EventMetadata
import com.szastarek.gymz.event.store.model.EventMetadataBuilder
import com.szastarek.gymz.event.store.model.EventType
import kotlinx.serialization.Serializable

@Serializable
data class AccountCreated(
    val id: String,
    val name: String,
    override val metadata: EventMetadata,
) : DomainEvent<AccountCreated> {

    companion object {
        fun metadata(accountId: String, causedBy: EventMetadata? = null) = EventMetadataBuilder(
            AggregateId(accountId),
            EventCategory("account"),
            EventType("account-created"),
        ).causedBy(causedBy).build()
    }

    override fun withMetadata(metadata: EventMetadata): AccountCreated {
        return copy(metadata = metadata)
    }
}

@Serializable
data class AccountNameUpdated(
    val id: String,
    val name: String,
    override val metadata: EventMetadata,
) : DomainEvent<AccountNameUpdated> {

    companion object {
        fun metadata(accountId: String, causedBy: EventMetadata? = null) = EventMetadataBuilder(
            AggregateId(accountId),
            EventCategory("account"),
            EventType("account-name-updated"),
        ).causedBy(causedBy).build()
    }

    override fun withMetadata(metadata: EventMetadata): AccountNameUpdated {
        return copy(metadata = metadata)
    }
}
