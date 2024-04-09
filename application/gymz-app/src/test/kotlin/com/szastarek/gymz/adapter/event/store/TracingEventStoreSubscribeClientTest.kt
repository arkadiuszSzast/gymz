package com.szastarek.gymz.adapter.event.store

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString.parseOrThrow
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.szastarek.gymz.event.store.AccountCreated
import com.szastarek.gymz.event.store.EventStoreContainer
import com.szastarek.gymz.event.store.EventStoreContainerFactory
import com.szastarek.gymz.event.store.EventStoreLifecycleListener
import com.szastarek.gymz.event.store.adapter.EventStoreDbSubscribeClient
import com.szastarek.gymz.event.store.adapter.EventStoreDbWriteClient
import com.szastarek.gymz.event.store.model.ConsumerGroup
import com.szastarek.gymz.event.store.model.ExpectedRevision
import com.szastarek.gymz.event.store.service.appendToStream
import com.szastarek.gymz.utils.InMemoryOpenTelemetry
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.events.Events
import kotlinx.serialization.json.Json
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

class TracingEventStoreSubscribeClientTest : StringSpec() {
    private val eventStoreContainer: EventStoreContainer = EventStoreContainerFactory.spawn()
    private val subscriptionClient = EventStoreDBPersistentSubscriptionsClient.create(parseOrThrow(eventStoreContainer.url))
    private val client = EventStoreDBClient.create(parseOrThrow(eventStoreContainer.url))
    private val json = Json

    private val openTelemetry = InMemoryOpenTelemetry()
    private val delegate = EventStoreDbSubscribeClient(subscriptionClient, json, Events())
    private val writeClient = TracingEventStoreWriteClient(EventStoreDbWriteClient(client, json), openTelemetry.get())

    private val tracingSubscribeClient = TracingEventStoreSubscribeClient(delegate, openTelemetry.get())

    init {
        listener(EventStoreLifecycleListener(eventStoreContainer))

        beforeTest {
            openTelemetry.reset()
        }

        "should process event in child span" {
            // arrange
            val accountId = UUID.randomUUID().toString()
            val event = AccountCreated(accountId, "Joe", AccountCreated.metadata(accountId))
            val eventMetadata = event.metadata
            val processedEventsCount = AtomicInteger(0)

            writeClient.appendToStream<AccountCreated>(event, ExpectedRevision.Any)
            val appenderSpan = openTelemetry.getFinishedSpans().single()

            // act
            val subscription = tracingSubscribeClient.subscribePersistentByEventType(
                eventMetadata.eventType,
                ConsumerGroup(UUID.randomUUID().toString()),
                AccountCreated::class,
            ) { _, processingEvent ->
                if (processingEvent.metadata.eventId == event.metadata.eventId) {
                    processedEventsCount.incrementAndGet()
                }
            }

            eventually(10.seconds) {
                processedEventsCount.get() shouldBe 1
            }

            // assert
            val subscriberSpan = openTelemetry.getFinishedSpans().last()
            subscriberSpan.parentSpanId shouldBe appenderSpan.spanContext.spanId
            subscription.stop()
        }
    }
}
