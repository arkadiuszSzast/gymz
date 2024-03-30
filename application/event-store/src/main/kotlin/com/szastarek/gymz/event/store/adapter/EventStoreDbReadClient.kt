package com.szastarek.gymz.event.store.adapter

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.StreamNotFoundException
import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventType
import com.szastarek.gymz.event.store.model.ReadStreamOptions
import com.szastarek.gymz.event.store.model.StreamName
import com.szastarek.gymz.event.store.service.EventStoreReadClient
import kotlinx.coroutines.future.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

@OptIn(ExperimentalSerializationApi::class)
class EventStoreDbReadClient(
    private val eventStoreDBClient: EventStoreDBClient,
    private val json: Json,
) : EventStoreReadClient {
    override suspend fun <T : DomainEvent> readStreamByEventType(
        eventType: EventType,
        clazz: KClass<T>,
        options: ReadStreamOptions,
    ): List<T> {
        return readStream(StreamName("\$et-${eventType.value}"), clazz, options)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : DomainEvent> readStream(
        streamName: StreamName,
        clazz: KClass<T>,
        options: ReadStreamOptions,
    ): List<T> {
        val rawEvents =
            try {
                eventStoreDBClient.readStream(streamName.value, options.toEventStoreDb()).await().events
            } catch (ex: StreamNotFoundException) {
                emptyList()
            }

        return rawEvents
            .map { json.decodeFromStream(serializer(clazz.createType()), it.event.eventData.inputStream()) as T }
    }
}
