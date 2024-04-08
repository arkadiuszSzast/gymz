package com.szastarek.gymz.event.store.service

import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventType
import com.szastarek.gymz.event.store.model.ReadStreamOptions
import com.szastarek.gymz.event.store.model.StreamName
import kotlin.reflect.KClass

interface EventStoreReadClient {
    suspend fun <T : DomainEvent<T>> readStream(
        streamName: StreamName,
        clazz: KClass<T>,
        options: ReadStreamOptions = ReadStreamOptions(),
    ): List<T>

    suspend fun <T : DomainEvent<T>> readStreamByEventType(
        eventType: EventType,
        clazz: KClass<T>,
        options: ReadStreamOptions = ReadStreamOptions(),
    ): List<T>
}

suspend inline fun <reified T : DomainEvent<T>> EventStoreReadClient.readStream(
    streamName: StreamName,
    options: ReadStreamOptions = ReadStreamOptions(),
) = readStream(streamName, T::class, options)

suspend inline fun <reified T : DomainEvent<T>> EventStoreReadClient.readStreamByEventType(
    eventType: EventType,
    options: ReadStreamOptions = ReadStreamOptions(),
) = readStreamByEventType(eventType, T::class, options)
