package com.szastarek.gymz.event.store.model

interface DomainEvent<T : DomainEvent<T>> {
    val metadata: EventMetadata

    fun withMetadata(metadata: EventMetadata): T
}
