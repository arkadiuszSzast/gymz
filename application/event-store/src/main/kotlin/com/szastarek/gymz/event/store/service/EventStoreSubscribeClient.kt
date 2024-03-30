package com.szastarek.gymz.event.store.service

import com.szastarek.gymz.event.store.model.ConsumerGroup
import com.szastarek.gymz.event.store.model.EventCategory
import com.szastarek.gymz.event.store.model.EventType
import com.szastarek.gymz.event.store.model.PersistentEventListener
import com.szastarek.gymz.event.store.model.PersistentSubscription
import com.szastarek.gymz.event.store.model.PersistentSubscriptionOptions

interface EventStoreSubscribeClient {
    suspend fun subscribePersistentByEventCategory(
        eventCategory: EventCategory,
        consumerGroup: ConsumerGroup,
        options: PersistentSubscriptionOptions = PersistentSubscriptionOptions(),
        listener: PersistentEventListener,
    ): PersistentSubscription

    suspend fun subscribePersistentByEventType(
        eventType: EventType,
        consumerGroup: ConsumerGroup,
        options: PersistentSubscriptionOptions = PersistentSubscriptionOptions(),
        listener: PersistentEventListener,
    ): PersistentSubscription
}

