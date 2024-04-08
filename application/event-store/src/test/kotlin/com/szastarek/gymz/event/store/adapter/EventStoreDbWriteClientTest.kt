package com.szastarek.gymz.event.store.adapter

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString.parseOrThrow
import com.szastarek.gymz.event.store.AccountCreated
import com.szastarek.gymz.event.store.EventStoreContainerFactory
import com.szastarek.gymz.event.store.EventStoreLifecycleListener
import com.szastarek.gymz.event.store.model.ExpectedRevision
import com.szastarek.gymz.event.store.model.InvalidExpectedRevisionException
import com.szastarek.gymz.event.store.service.appendToStream
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import java.util.UUID

class EventStoreDbWriteClientTest : StringSpec() {
    private val eventStoreContainer = EventStoreContainerFactory.spawn()

    private val json = Json
    private val eventStoreDbClient = EventStoreDBClient.create(parseOrThrow(eventStoreContainer.url))

    private val eventStoreDbWriteClient = EventStoreDbWriteClient(eventStoreDbClient, json)

    init {
        listener(EventStoreLifecycleListener(eventStoreContainer))

        "should append event" {
            // arrange
            val accountId = UUID.randomUUID().toString()
            val event = AccountCreated(
                accountId,
                "Joe",
                AccountCreated.metadata(accountId),
            )

            // act
            val result = eventStoreDbWriteClient.appendToStream(event, ExpectedRevision.NoStream)

            // assert
            result.nextExpectedRevision.toEventStoreDb().toRawLong() shouldBe 0
        }

        "should throw exception when appending event with wrong expected revision" {
            // arrange
            val accountId = UUID.randomUUID().toString()
            val event = AccountCreated(
                accountId,
                "Joe",
                AccountCreated.metadata(accountId),
            )

            // act && assert
            shouldThrow<InvalidExpectedRevisionException> {
                eventStoreDbWriteClient.appendToStream(event, ExpectedRevision.Exact(1))
            }
        }
    }
}
