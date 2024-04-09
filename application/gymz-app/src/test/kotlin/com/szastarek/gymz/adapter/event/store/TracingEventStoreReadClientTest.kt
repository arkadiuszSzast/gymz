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
import com.szastarek.gymz.event.store.model.streamName
import com.szastarek.gymz.event.store.service.appendToStream
import com.szastarek.gymz.event.store.service.readStream
import com.szastarek.gymz.utils.InMemoryOpenTelemetry
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import java.util.UUID

class TracingEventStoreReadClientTest : StringSpec() {

    private val eventStoreContainer: EventStoreContainer = EventStoreContainerFactory.spawn()
    private val client = EventStoreDBClient.create(parseOrThrow(eventStoreContainer.url))

    private val json = Json
    private val openTelemetry = InMemoryOpenTelemetry()

    private val writeClient = EventStoreDbWriteClient(client, json)
    private val delegate = EventStoreDbReadClient(client, json)
    private val readClient = TracingEventStoreReadClient(delegate, openTelemetry.get())

    init {
        listener(EventStoreLifecycleListener(eventStoreContainer))

        beforeTest {
            openTelemetry.reset()
        }

        "should read stream in child span" {
            // arrange
            val accountId = UUID.randomUUID().toString()
            val event = AccountCreated(
                accountId,
                "Joe",
                AccountCreated.metadata(accountId),
            )
            writeClient.appendToStream(event, ExpectedRevision.NoStream)

            // act
            readClient.readStream<AccountCreated>(event.metadata.streamName)

            // assert
            openTelemetry.getFinishedSpans()
                .single().name shouldBe "event_store read ${event.metadata.streamName.value}"
        }
    }
}
