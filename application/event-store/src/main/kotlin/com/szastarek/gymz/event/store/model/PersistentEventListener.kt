package com.szastarek.gymz.event.store.model

typealias PersistentEventListener = suspend (subscription: PersistentSubscription, event: DomainEvent) -> Unit
