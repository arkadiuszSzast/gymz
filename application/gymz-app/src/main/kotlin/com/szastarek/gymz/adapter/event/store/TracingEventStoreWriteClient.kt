package com.szastarek.gymz.adapter.event.store

import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventMetadataBuilder
import com.szastarek.gymz.event.store.model.EventStoreWriteResult
import com.szastarek.gymz.event.store.model.ExpectedRevision
import com.szastarek.gymz.event.store.service.EventStoreWriteClient
import com.szastarek.gymz.shared.monitoring.execute
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.context.Context
import io.opentelemetry.context.propagation.TextMapSetter
import kotlin.reflect.KClass

class TracingEventStoreWriteClient(
    private val delegate: EventStoreWriteClient,
    private val openTelemetry: OpenTelemetry,
) : EventStoreWriteClient {
    override suspend fun <T : DomainEvent<T>> appendToStream(
        event: T,
        clazz: KClass<T>,
        expectedRevision: ExpectedRevision,
    ): EventStoreWriteResult {
        val tracer = openTelemetry.getTracer("event-store-db")

        return tracer.spanBuilder("event_store publish ${event.metadata.eventType}")
            .setAttribute("db.system", "eventstore-db")
            .startSpan().execute {
                val eventMetadata = EventMetadataBuilder.fromPrototype(event.metadata).apply {
                    openTelemetry.propagators.textMapPropagator.inject(
                        Context.current(),
                        this,
                        eventStoreDbTracingContextSetter,
                    )
                }.build()
                val eventWithTracingContext = event.withMetadata(eventMetadata)
                delegate.appendToStream(eventWithTracingContext, clazz, expectedRevision)
            }
    }
}

private val eventStoreDbTracingContextSetter =
    TextMapSetter<EventMetadataBuilder> { carrier, key, value ->
        if (carrier == null) {
            return@TextMapSetter
        }
        carrier.withProperty(key, value)
    }
