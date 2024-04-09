package com.szastarek.gymz.adapter.event.store

import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventType
import com.szastarek.gymz.event.store.model.ReadStreamOptions
import com.szastarek.gymz.event.store.model.StreamName
import com.szastarek.gymz.event.store.service.EventStoreReadClient
import com.szastarek.gymz.shared.monitoring.execute
import io.opentelemetry.api.OpenTelemetry
import kotlin.reflect.KClass

class TracingEventStoreReadClient(
    private val delegate: EventStoreReadClient,
    private val openTelemetry: OpenTelemetry,
) : EventStoreReadClient {
    override suspend fun <T : DomainEvent<T>> readStream(
        streamName: StreamName,
        clazz: KClass<T>,
        options: ReadStreamOptions,
    ): List<T> {
        val tracer = openTelemetry.getTracer("event-store-db")
        return tracer.spanBuilder("event_store read ${streamName.value}")
            .setAttribute("db.system", "eventstore-db")
            .startSpan()
            .execute {
                delegate.readStream(streamName, clazz, options)
            }
    }

    override suspend fun <T : DomainEvent<T>> readStreamByEventType(
        eventType: EventType,
        clazz: KClass<T>,
        options: ReadStreamOptions,
    ): List<T> {
        val tracer = openTelemetry.getTracer("event-store-db")
        return tracer.spanBuilder("event_store read ${eventType.value}")
            .setAttribute("db.system", "eventstore-db")
            .startSpan()
            .execute {
                delegate.readStreamByEventType(eventType, clazz, options)
            }
    }
}
