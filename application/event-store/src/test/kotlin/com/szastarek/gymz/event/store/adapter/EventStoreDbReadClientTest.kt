package com.szastarek.gymz.event.store.adapter

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString.parseOrThrow
import com.szastarek.gymz.event.store.AccountCreated
import com.szastarek.gymz.event.store.EventStoreContainerFactory
import com.szastarek.gymz.event.store.EventStoreLifecycleListener
import com.szastarek.gymz.event.store.model.ExpectedRevision
import com.szastarek.gymz.event.store.model.streamName
import com.szastarek.gymz.event.store.service.appendToStream
import com.szastarek.gymz.event.store.service.readStream
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import java.util.UUID

class EventStoreDbReadClientTest : StringSpec() {
    private val eventStoreContainer = EventStoreContainerFactory.spawn()

    private val eventStoreDbClient = EventStoreDBClient.create(parseOrThrow(eventStoreContainer.url))
    private val json = Json

    private val readClient = EventStoreDbReadClient(eventStoreDbClient, json)
    private val writeClient = EventStoreDbWriteClient(eventStoreDbClient, json)

    init {
        listener(EventStoreLifecycleListener(eventStoreContainer))

        "should read stream" {
            // arrange
            val accountId = UUID.randomUUID().toString()
            val event = AccountCreated(
                accountId,
                "Joe",
                AccountCreated.metadata(accountId),
            )
            writeClient.appendToStream(event, ExpectedRevision.NoStream)

            // act
            val result = readClient.readStream<AccountCreated>(event.metadata.streamName)

            // assert
            result shouldBe listOf(event)
        }
    }
}
