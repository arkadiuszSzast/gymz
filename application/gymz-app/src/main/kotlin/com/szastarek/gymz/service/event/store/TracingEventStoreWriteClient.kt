package com.szastarek.gymz.service.event.store

import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventMetadata
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
    ) : EventStoreWriteClient{
    override suspend fun <T : DomainEvent> appendToStream(
        event: T,
        clazz: KClass<T>,
        expectedRevision: ExpectedRevision,
        causedBy: EventMetadata?
    ): EventStoreWriteResult {
        val tracer = openTelemetry.getTracer("event-store-db")
        val eventMetadata = EventMetadataBuilder.fromPrototype(event.getMetadata(causedBy)).apply {
            openTelemetry.propagators.textMapPropagator.inject(
                Context.current(),
                this,
                eventStoreDbTracingContextSetter,
            )
        }.build()

        tracer.spanBuilder("event_store publish ${eventMetadata.eventType.value}")
            .setAttribute("db.system", "eventstore-db")
            .startSpan().execute {

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