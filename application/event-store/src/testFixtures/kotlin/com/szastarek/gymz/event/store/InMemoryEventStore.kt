package com.szastarek.gymz.event.store

import com.szastarek.gymz.event.store.adapter.toEventStoreDb
import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventStoreWriteResult
import com.szastarek.gymz.event.store.model.EventType
import com.szastarek.gymz.event.store.model.ExpectedRevision
import com.szastarek.gymz.event.store.model.InvalidExpectedRevisionException
import com.szastarek.gymz.event.store.model.Position
import com.szastarek.gymz.event.store.model.ReadStreamOptions
import com.szastarek.gymz.event.store.model.StreamName
import com.szastarek.gymz.event.store.model.streamName
import com.szastarek.gymz.event.store.service.EventStoreReadClient
import com.szastarek.gymz.event.store.service.EventStoreWriteClient
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class InMemoryEventStore : EventStoreReadClient, EventStoreWriteClient {
    private val eventsByStreamName: MutableMap<StreamName, List<DomainEvent<*>>?> = mutableMapOf()
    private val eventsByType: MutableMap<EventType, List<DomainEvent<*>>?> = mutableMapOf()
    private val mutex = Mutex()

    override suspend fun <T : DomainEvent<T>> readStream(
        streamName: StreamName,
        clazz: KClass<T>,
        options: ReadStreamOptions,
    ): List<T> = mutex.withLock {
        eventsByStreamName[streamName].orEmpty().map { it as T }
    }

    override suspend fun <T : DomainEvent<T>> readStreamByEventType(
        eventType: EventType,
        clazz: KClass<T>,
        options: ReadStreamOptions,
    ): List<T> = mutex.withLock {
        eventsByType[eventType].orEmpty().map { it as T }
    }

    override suspend fun <T : DomainEvent<T>> appendToStream(
        event: T,
        clazz: KClass<T>,
        expectedRevision: ExpectedRevision,
    ): EventStoreWriteResult {
        mutex.withLock {
            val streamName = event.metadata.streamName
            val existingEvents = eventsByStreamName[streamName]
            if (!expectedRevision.isValid(existingEvents)) {
                throw InvalidExpectedRevisionException(
                    streamName.value,
                    expectedRevision.toEventStoreDb(),
                    existingEvents.toRevision().toEventStoreDb(),
                )
            }

            eventsByStreamName[streamName] = existingEvents.orEmpty() + event
            eventsByType[event.metadata.eventType] = existingEvents.orEmpty() + event
            return EventStoreWriteResult(
                logPosition = Position(eventsByStreamName.size.toLong(), eventsByStreamName.size.toLong() + 1),
                nextExpectedRevision = ExpectedRevision.Exact(eventsByStreamName.size.toLong() + 1),
            )
        }
    }

    suspend fun clear() = mutex.withLock {
        eventsByStreamName.clear()
        eventsByType.clear()
    }

    private fun List<DomainEvent<*>>?.toRevision() =
        when (this) {
            null -> ExpectedRevision.NoStream
            else -> ExpectedRevision.Exact(this.size.toLong())
        }

    private fun ExpectedRevision.isValid(events: List<DomainEvent<*>>?) = when (this) {
        is ExpectedRevision.Any -> true
        is ExpectedRevision.NoStream -> events == null
        is ExpectedRevision.StreamExists -> events != null
        is ExpectedRevision.Exact -> events?.size == revision.toInt()
    }
}
