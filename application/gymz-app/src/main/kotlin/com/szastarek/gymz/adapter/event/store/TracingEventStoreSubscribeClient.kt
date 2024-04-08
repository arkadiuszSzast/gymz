package com.szastarek.gymz.adapter.event.store

import com.szastarek.gymz.event.store.model.ActivePersistentSubscription
import com.szastarek.gymz.event.store.model.ConsumerGroup
import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventCategory
import com.szastarek.gymz.event.store.model.EventMetadata
import com.szastarek.gymz.event.store.model.EventType
import com.szastarek.gymz.event.store.model.PersistentEventListener
import com.szastarek.gymz.event.store.model.PersistentSubscriptionOptions
import com.szastarek.gymz.event.store.service.EventStoreSubscribeClient
import com.szastarek.gymz.shared.monitoring.execute
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.context.Context
import io.opentelemetry.context.propagation.TextMapGetter
import kotlin.reflect.KClass

class TracingEventStoreSubscribeClient(
    private val delegate: EventStoreSubscribeClient,
    private val openTelemetry: OpenTelemetry,
) : EventStoreSubscribeClient {

    override suspend fun <T : DomainEvent<T>> subscribePersistentByEventCategory(
        eventCategory: EventCategory,
        consumerGroup: ConsumerGroup,
        clazz: KClass<T>,
        options: PersistentSubscriptionOptions,
        listener: PersistentEventListener<T>,
    ): ActivePersistentSubscription = delegate.subscribePersistentByEventCategory(
        eventCategory,
        consumerGroup,
        clazz,
        options,
        tracingPersistentListener(listener, consumerGroup),
    )

    override suspend fun <T : DomainEvent<T>> subscribePersistentByEventType(
        eventType: EventType,
        consumerGroup: ConsumerGroup,
        clazz: KClass<T>,
        options: PersistentSubscriptionOptions,
        listener: PersistentEventListener<T>,
    ): ActivePersistentSubscription = delegate.subscribePersistentByEventType(
        eventType,
        consumerGroup,
        clazz,
        options,
        tracingPersistentListener(listener, consumerGroup),
    )

    private suspend fun <T : DomainEvent<T>> tracingPersistentListener(
        listener: PersistentEventListener<T>,
        consumerGroup: ConsumerGroup,
    ): PersistentEventListener<T> {
        return { subscription, event ->
            val textMapPropagator = openTelemetry.propagators.textMapPropagator
            val extractedContext = textMapPropagator.extract(Context.current(), event.metadata, EventStoreDbGetter)
            extractedContext.makeCurrent().use {
                openTelemetry.getTracer("persistent-event-listener")
                    .spanBuilder(consumerGroup.value)
                    .setAttribute("db.system", "eventstore-db")
                    .setAttribute("eventType", event.metadata.eventType.value)
                    .setSpanKind(SpanKind.SERVER)
                    .startSpan()
                    .execute {
                        listener(subscription, event)
                    }
            }
        }
    }
}

private object EventStoreDbGetter : TextMapGetter<EventMetadata> {
    override fun keys(carrier: EventMetadata): MutableIterable<String> {
        return carrier.customData.keys.toMutableList()
    }

    override fun get(
        carrier: EventMetadata?,
        key: String,
    ): String? {
        if (carrier == null) {
            return null
        }

        return carrier.customData[key]
    }
}
