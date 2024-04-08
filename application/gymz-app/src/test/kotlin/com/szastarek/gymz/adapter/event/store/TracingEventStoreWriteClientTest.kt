package com.szastarek.gymz.adapter.event.store

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString.parseOrThrow
import com.szastarek.gymz.event.store.AccountCreated
import com.szastarek.gymz.event.store.EventStoreContainer
import com.szastarek.gymz.event.store.EventStoreContainerFactory
import com.szastarek.gymz.event.store.EventStoreLifecycleListener
import com.szastarek.gymz.event.store.adapter.EventStoreDbReadClient
import com.szastarek.gymz.event.store.adapter.EventStoreDbWriteClient
import com.szastarek.gymz.event.store.model.ExpectedRevision
import com.szastarek.gymz.event.store.service.appendToStream
import com.szastarek.gymz.event.store.service.readStreamByEventType
import com.szastarek.gymz.utils.InMemoryOpenTelemetry
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import java.util.UUID

class TracingEventStoreWriteClientTest : StringSpec() {

    private val eventStoreContainer: EventStoreContainer = EventStoreContainerFactory.spawn()
    private val client = EventStoreDBClient.create(parseOrThrow(eventStoreContainer.url))

    private val json = Json
    private val openTelemetry = InMemoryOpenTelemetry()

    private val readClient = EventStoreDbReadClient(client, json)
    private val delegate = EventStoreDbWriteClient(client, json)
    private val tracingWriteClient = TracingEventStoreWriteClient(delegate, openTelemetry.get())

    init {
        listener(EventStoreLifecycleListener(eventStoreContainer))

        beforeTest {
            openTelemetry.reset()
        }

        "should append span details to event metadata" {
            // arrange
            val accountId = UUID.randomUUID().toString()
            val event = AccountCreated(
                accountId,
                "Joe",
                AccountCreated.metadata(accountId),
            )
            // act
            tracingWriteClient.appendToStream(event, ExpectedRevision.NoStream)

            // assert
            val span = openTelemetry.getFinishedSpans().single()
            val accountCreatedMetadata =
                readClient.readStreamByEventType<AccountCreated>(event.metadata.eventType)
                    .first().metadata

            accountCreatedMetadata.customData["traceparent"] shouldBe "00-${span.traceId}-${span.spanId}-01"
        }
    }
}
