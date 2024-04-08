package com.szastarek.gymz.event.store.model

typealias PersistentEventListener<T> = suspend (subscription: PersistentSubscription<T>, event: DomainEvent<T>) -> Unit
