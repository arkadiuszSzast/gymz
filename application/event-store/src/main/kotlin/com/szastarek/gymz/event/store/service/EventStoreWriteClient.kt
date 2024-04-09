package com.szastarek.gymz.event.store.service

import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventStoreWriteResult
import com.szastarek.gymz.event.store.model.ExpectedRevision
import kotlin.reflect.KClass

interface EventStoreWriteClient {

    suspend fun <T : DomainEvent<T>> appendToStream(
        event: T,
        clazz: KClass<T>,
        expectedRevision: ExpectedRevision,
    ): EventStoreWriteResult
}

suspend inline fun <reified T : DomainEvent<T>> EventStoreWriteClient.appendToStream(
    event: T,
    expectedRevision: ExpectedRevision,
) = appendToStream(event, T::class, expectedRevision)
