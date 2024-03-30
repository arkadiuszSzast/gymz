package com.szastarek.gymz.event.store.service

import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventType
import com.szastarek.gymz.event.store.model.ReadStreamOptions
import com.szastarek.gymz.event.store.model.StreamName
import kotlin.reflect.KClass

interface EventStoreReadClient {
	suspend fun <T : DomainEvent> readStream(
		streamName: StreamName,
		clazz: KClass<T>,
		options: ReadStreamOptions = ReadStreamOptions(),
	): List<T>

	suspend fun <T : DomainEvent> readStreamByEventType(
		eventType: EventType,
		clazz: KClass<T>,
		options: ReadStreamOptions = ReadStreamOptions(),
	): List<T>
}

suspend inline fun <reified T : DomainEvent> EventStoreReadClient.readStream(
	streamName: StreamName,
	options: ReadStreamOptions = ReadStreamOptions(),
) = readStream(streamName, T::class, options)

suspend inline fun <reified T : DomainEvent> EventStoreReadClient.readStreamByEventType(
	eventType: EventType,
	options: ReadStreamOptions = ReadStreamOptions(),
) = readStreamByEventType(eventType, T::class, options)
