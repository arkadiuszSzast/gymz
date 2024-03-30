package com.szastarek.gymz.event.store.service

import com.szastarek.gymz.event.store.model.DomainEvent
import com.szastarek.gymz.event.store.model.EventMetadata
import com.szastarek.gymz.event.store.model.EventStoreWriteResult
import com.szastarek.gymz.event.store.model.ExpectedRevision
import kotlin.reflect.KClass

interface EventStoreWriteClient {

	suspend fun <T : DomainEvent> appendToStream(
		event: T,
		clazz: KClass<T>,
		expectedRevision: ExpectedRevision,
		causedBy: EventMetadata? = null,
	): EventStoreWriteResult
}

suspend inline fun <reified T : DomainEvent> EventStoreWriteClient.appendToStream(
	event: T,
	expectedRevision: ExpectedRevision,
	causedBy: EventMetadata? = null,
) = appendToStream(event, T::class, expectedRevision, causedBy)
