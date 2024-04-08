package com.szastarek.gymz.event.store.service

import com.szastarek.gymz.event.store.model.ActivePersistentSubscription
import com.szastarek.gymz.event.store.model.ConsumerGroup
import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventCategory
import com.szastarek.gymz.event.store.model.EventType
import com.szastarek.gymz.event.store.model.PersistentEventListener
import com.szastarek.gymz.event.store.model.PersistentSubscriptionOptions
import kotlin.reflect.KClass

interface EventStoreSubscribeClient {
    suspend fun <T : DomainEvent<T>> subscribePersistentByEventCategory(
        eventCategory: EventCategory,
        consumerGroup: ConsumerGroup,
        clazz: KClass<T>,
        options: PersistentSubscriptionOptions = PersistentSubscriptionOptions(),
        listener: PersistentEventListener<T>,
    ): ActivePersistentSubscription

    suspend fun <T : DomainEvent<T>> subscribePersistentByEventType(
        eventType: EventType,
        consumerGroup: ConsumerGroup,
        clazz: KClass<T>,
        options: PersistentSubscriptionOptions = PersistentSubscriptionOptions(),
        listener: PersistentEventListener<T>,
    ): ActivePersistentSubscription
}

suspend inline fun <reified T : DomainEvent<T>> EventStoreSubscribeClient.subscribePersistentByEventCategory(
    eventCategory: EventCategory,
    consumerGroup: ConsumerGroup,
    options: PersistentSubscriptionOptions = PersistentSubscriptionOptions(),
    noinline listener: PersistentEventListener<T>,
): ActivePersistentSubscription = subscribePersistentByEventCategory(
    eventCategory,
    consumerGroup,
    T::class,
    options,
    listener,
)

suspend inline fun <reified T : DomainEvent<T>> EventStoreSubscribeClient.subscribePersistentByEventType(
    eventType: EventType,
    consumerGroup: ConsumerGroup,
    options: PersistentSubscriptionOptions = PersistentSubscriptionOptions(),
    noinline listener: PersistentEventListener<T>,
): ActivePersistentSubscription = subscribePersistentByEventType(
    eventType,
    consumerGroup,
    T::class,
    options,
    listener,
)
