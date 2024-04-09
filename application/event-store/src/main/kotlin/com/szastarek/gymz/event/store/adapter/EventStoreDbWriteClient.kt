package com.szastarek.gymz.event.store.adapter

import com.eventstore.dbclient.AppendToStreamOptions
import com.eventstore.dbclient.EventDataBuilder
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.WrongExpectedVersionException
import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventMetadata
import com.szastarek.gymz.event.store.model.EventStoreWriteResult
import com.szastarek.gymz.event.store.model.ExpectedRevision
import com.szastarek.gymz.event.store.model.InvalidExpectedRevisionException
import com.szastarek.gymz.event.store.model.streamName
import com.szastarek.gymz.event.store.service.EventStoreWriteClient
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

class EventStoreDbWriteClient(
    private val eventStoreDBClient: EventStoreDBClient,
    private val json: Json,
) : EventStoreWriteClient {

    override suspend fun <T : DomainEvent<T>> appendToStream(
        event: T,
        clazz: KClass<T>,
        expectedRevision: ExpectedRevision,
    ): EventStoreWriteResult {
        return append(event, clazz, AppendToStreamOptions.get().expectedRevision(expectedRevision.toEventStoreDb()))
    }

    private suspend fun <T : DomainEvent<T>> append(
        event: T,
        clazz: KClass<T>,
        appendToStreamOptions: AppendToStreamOptions,
    ): EventStoreWriteResult {
        val eventMetadata = event.metadata
        val eventBytes = json.encodeToString(serializer(clazz.createType()), event).encodeToByteArray()
        val metadataBytes =
            json.encodeToString(EventMetadata.serializer(), eventMetadata).encodeToByteArray()

        val eventData =
            EventDataBuilder.json(eventMetadata.eventId.value, eventMetadata.eventType.value, eventBytes)
                .metadataAsBytes(metadataBytes).build()

        return try {
            eventStoreDBClient.appendToStream(
                eventMetadata.streamName.value,
                appendToStreamOptions,
                eventData,
            ).await().let { EventStoreWriteResult(it.logPosition.toDomain(), it.nextExpectedRevision.toDomain()) }
        } catch (ex: WrongExpectedVersionException) {
            throw InvalidExpectedRevisionException(ex.streamName, ex.nextExpectedRevision, ex.actualVersion)
        }
    }
}
