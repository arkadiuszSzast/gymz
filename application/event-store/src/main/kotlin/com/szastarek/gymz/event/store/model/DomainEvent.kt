package com.szastarek.gymz.event.store.model

interface DomainEvent {
	fun getMetadata(causedBy: EventMetadata? = null): EventMetadata
}
