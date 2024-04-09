package com.szastarek.gymz.event.store.adapter

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString.parseOrThrow
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.szastarek.gymz.event.store.AccountCreated
import com.szastarek.gymz.event.store.EventStoreContainer
import com.szastarek.gymz.event.store.EventStoreContainerFactory
import com.szastarek.gymz.event.store.EventStoreLifecycleListener
import com.szastarek.gymz.event.store.model.ConsumerGroup
import com.szastarek.gymz.event.store.model.ExpectedRevision
import com.szastarek.gymz.event.store.model.PersistentSubscriptionOptions
import com.szastarek.gymz.event.store.service.appendToStream
import com.szastarek.gymz.event.store.service.subscribePersistentByEventCategory
import com.szastarek.gymz.event.store.service.subscribePersistentByEventType
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.events.Events
import kotlinx.serialization.json.Json
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

class EventStoreDbSubscribeClientTest : StringSpec() {

    private val eventStoreContainer: EventStoreContainer = EventStoreContainerFactory.spawn()
    private val subscriptionClient = EventStoreDBPersistentSubscriptionsClient.create(parseOrThrow(eventStoreContainer.url))
    private val client = EventStoreDBClient.create(parseOrThrow(eventStoreContainer.url))
    private val json = Json

    private val subscribeClient = EventStoreDbSubscribeClient(subscriptionClient, json, Events())
    private val writeClient = EventStoreDbWriteClient(client, json)

    init {
        listener(EventStoreLifecycleListener(eventStoreContainer))

        "should subscribe by event category and process event" {
            // arrange
            val accountId = UUID.randomUUID().toString()
            val event = AccountCreated(
                accountId,
                "Joe",
                AccountCreated.metadata(accountId),
            )
            val processedEventsCount = AtomicInteger(0)
            writeClient.appendToStream(event, ExpectedRevision.Any)

            // act
            val subscription = subscribeClient.subscribePersistentByEventCategory<AccountCreated>(
                event.metadata.eventCategory,
                ConsumerGroup(UUID.randomUUID().toString()),
            ) { _, processingEvent ->
                if (processingEvent == event) {
                    processedEventsCount.incrementAndGet()
                }
            }

            // assert
            eventually(10.seconds) {
                processedEventsCount.get() shouldBe 1
            }
            subscription.stop()
        }

        "should subscribe by event type and process event" {
            // arrange
            val accountId = UUID.randomUUID().toString()
            val event = AccountCreated(
                accountId,
                "Joe",
                AccountCreated.metadata(accountId),
            )
            val processedEventsCount = AtomicInteger(0)
            writeClient.appendToStream(event, ExpectedRevision.Any)

            // act
            val subscription = subscribeClient.subscribePersistentByEventType<AccountCreated>(
                event.metadata.eventType,
                ConsumerGroup(UUID.randomUUID().toString()),
            ) { _, processingEvent ->
                if (processingEvent == event) {
                    processedEventsCount.incrementAndGet()
                }
            }

            // assert
            eventually(10.seconds) {
                processedEventsCount.get() shouldBe 1
            }
            subscription.stop()
        }

        "should retry processing event when exception is thrown" {
            // arrange
            val accountId = UUID.randomUUID().toString()
            val event = AccountCreated(
                accountId,
                "Joe",
                AccountCreated.metadata(accountId),
            )
            val processedEventsCount = AtomicInteger(0)
            val attemptsCount = AtomicInteger(0)

            writeClient.appendToStream(event, ExpectedRevision.Any)

            // act
            val subscription = subscribeClient.subscribePersistentByEventType<AccountCreated>(
                event.metadata.eventType,
                ConsumerGroup(UUID.randomUUID().toString()),
                PersistentSubscriptionOptions(maxRetries = 3),
            ) { _, processingEvent ->
                if (attemptsCount.getAndIncrement() < 3) {
                    throw RuntimeException("ops!")
                }
                if (processingEvent == event) {
                    processedEventsCount.incrementAndGet()
                }
            }

            // assert
            eventually(10.seconds) {
                processedEventsCount.get() shouldBe 1
                attemptsCount.get() shouldBe 4
            }
            subscription.stop()
        }
    }
}
