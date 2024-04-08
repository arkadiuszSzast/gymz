package com.szastarek.gymz.event.store.adapter

import com.eventstore.dbclient.CreatePersistentSubscriptionToStreamOptions
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.eventstore.dbclient.NackAction
import com.eventstore.dbclient.PersistentSubscriptionListener
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.SubscribePersistentSubscriptionOptions
import com.szastarek.gymz.event.store.model.ActivePersistentSubscription
import com.szastarek.gymz.event.store.model.ConsumerGroup
import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventCategory
import com.szastarek.gymz.event.store.model.EventMetadata
import com.szastarek.gymz.event.store.model.EventType
import com.szastarek.gymz.event.store.model.NackReason
import com.szastarek.gymz.event.store.model.PersistentEventListener
import com.szastarek.gymz.event.store.model.PersistentSubscription
import com.szastarek.gymz.event.store.model.PersistentSubscriptionOptions
import com.szastarek.gymz.event.store.model.StreamName
import com.szastarek.gymz.event.store.model.SubscriptionId
import com.szastarek.gymz.event.store.service.EventStoreSubscribeClient
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.events.Events
import io.ktor.server.application.ApplicationStopPreparing
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import com.szastarek.gymz.event.store.model.NackAction as NackActionDomain

private val logger = KotlinLogging.logger {}

class EventStoreDbSubscribeClient(
    private val client: EventStoreDBPersistentSubscriptionsClient,
    private val json: Json,
    private val applicationEvents: Events,
) : EventStoreSubscribeClient, CoroutineScope {

    private val parent: CompletableJob = Job()
    override val coroutineContext: CoroutineContext
        get() = parent

    override suspend fun <T : DomainEvent<T>> subscribePersistentByEventCategory(
        eventCategory: EventCategory,
        consumerGroup: ConsumerGroup,
        clazz: KClass<T>,
        options: PersistentSubscriptionOptions,
        listener: PersistentEventListener<T>,
    ): ActivePersistentSubscription {
        applicationEvents.subscribe(ApplicationStopPreparing) {
            shutdown()
        }

        return subscribeToPersistentStream(
            StreamName("\$ce-${eventCategory.value}"),
            consumerGroup,
            clazz,
            options,
            listener,
        )
    }

    override suspend fun <T : DomainEvent<T>> subscribePersistentByEventType(
        eventType: EventType,
        consumerGroup: ConsumerGroup,
        clazz: KClass<T>,
        options: PersistentSubscriptionOptions,
        listener: PersistentEventListener<T>,
    ): ActivePersistentSubscription {
        applicationEvents.subscribe(ApplicationStopPreparing) {
            shutdown()
        }

        return subscribeToPersistentStream(
            StreamName("\$et-${eventType.value}"),
            consumerGroup,
            clazz,
            options,
            listener,
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private suspend fun <T : DomainEvent<T>> subscribeToPersistentStream(
        streamName: StreamName,
        customerGroup: ConsumerGroup,
        clazz: KClass<T>,
        options: PersistentSubscriptionOptions,
        listener: PersistentEventListener<T>,
    ): ActivePersistentSubscription {
        subscriptionContext.let { context ->
            createSubscriptionIfNeeded(streamName, customerGroup, options)

            return client.subscribeToStream(
                streamName.value,
                customerGroup.value,
                SubscribePersistentSubscriptionOptions.get().bufferSize(options.bufferSize.toInt()),
                object : PersistentSubscriptionListener() {
                    override fun onEvent(
                        subscription: com.eventstore.dbclient.PersistentSubscription,
                        retryCount: Int,
                        event: ResolvedEvent,
                    ) {
                        launch(context) {
                            runCatching {
                                val decodedEvent = json.decodeFromStream(serializer(clazz.createType()), event.event.eventData.inputStream()) as T
                                val metadata =
                                    json.decodeFromStream<EventMetadata>(event.event.userMetadata.inputStream())
                                val domainEvent = decodedEvent.withMetadata(metadata)
                                val resolvedEventByDomain = mapOf(domainEvent to event)

                                val domainSubscriber: PersistentSubscription<T> = object : PersistentSubscription<T> {
                                    override val subscriptionId: SubscriptionId
                                        get() = SubscriptionId(subscription.subscriptionId)

                                    override fun stop() {
                                        subscription.stop()
                                    }

                                    override fun nack(
                                        nackAction: NackActionDomain,
                                        reason: NackReason,
                                        vararg events: DomainEvent<T>,
                                    ) {
                                        val mappedEvents = events.mapNotNull {
                                            resolvedEventByDomain[it].also {
                                                if (it == null) {
                                                    logger.error { "Failed to nack event: $domainEvent" }
                                                }
                                            }
                                        }
                                        subscription.nack(nackAction.toEventStore(), reason.value, mappedEvents.iterator())
                                    }

                                    override fun ack(vararg events: DomainEvent<T>) {
                                        val mappedEvents = events.mapNotNull {
                                            resolvedEventByDomain[it].also {
                                                if (it == null) {
                                                    logger.error { "Failed to ack event: $domainEvent" }
                                                }
                                            }
                                        }
                                        subscription.ack(mappedEvents.iterator())
                                    }
                                }

                                listener(domainSubscriber, domainEvent)

                                if (options.autoAcknowledge) {
                                    subscription.ack(event)
                                }
                            }.onFailure { error ->
                                val eventId = event.originalEvent.eventId
                                if (retryCount < options.maxRetries) {
                                    logger.error(error) {
                                        "Error when processing event[$eventId]. Retry attempt [${retryCount + 1}/${options.maxRetries}]"
                                    }
                                    subscription.nack(
                                        NackAction.Retry,
                                        "exception_${error::class.simpleName}",
                                        event,
                                    )
                                } else {
                                    logger.error(error) {
                                        "Error when processing event[$eventId]. Going to ${options.nackAction.name} event"
                                    }
                                    subscription.nack(
                                        options.nackAction.toEventStore(),
                                        "exception_${error::class.simpleName}",
                                        event,
                                    )
                                }
                            }
                        }
                    }

                    override fun onCancelled(
                        subscription: com.eventstore.dbclient.PersistentSubscription?,
                        throwable: Throwable?,
                    ) {
                        launch(context) {
                            logger.error(throwable) { "Error on persisted subscription [${subscription?.subscriptionId}]" }
                            if (options.reSubscribeOnDrop && throwable != null) {
                                subscribeToPersistentStream(streamName, customerGroup, clazz, options, listener)
                            }
                        }
                    }
                },
            ).await().let { EventStoreDbActivePersistentSubscription(it) }
        }
    }

    private suspend fun createSubscriptionIfNeeded(
        streamName: StreamName,
        customerGroup: ConsumerGroup,
        options: PersistentSubscriptionOptions,
    ) {
        val consumerGroupExists = client.getInfoToStream(
            streamName.value,
            customerGroup.value,
        ).await().isPresent
        if (!consumerGroupExists && options.autoCreateStreamGroup) {
            logger.debug {
                "Stream group $customerGroup not found. AutoCreateStreamGroup is ON. Trying to create the group."
            }
            client.createToStream(
                streamName.value,
                customerGroup.value,
                CreatePersistentSubscriptionToStreamOptions.get().fromStart().resolveLinkTos(),
            ).await()
            logger.debug { "Stream group $customerGroup created." }
        }
    }

    private fun shutdown(): Boolean {
        return parent.complete().also { client.shutdown().get() }
    }

    private val subscriptionContextCounter = AtomicInteger(0)

    @OptIn(DelicateCoroutinesApi::class)
    private val subscriptionContext: ExecutorCoroutineDispatcher
        get() =
            newSingleThreadContext("EventStoreDB-subscription-context-${subscriptionContextCounter.incrementAndGet()}")
}
