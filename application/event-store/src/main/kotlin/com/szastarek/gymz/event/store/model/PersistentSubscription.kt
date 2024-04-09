package com.szastarek.gymz.event.store.model

interface PersistentSubscription<T : DomainEvent<T>> {
    val subscriptionId: SubscriptionId

    fun stop()
    fun ack(vararg events: DomainEvent<T>)
    fun nack(nackAction: NackAction, reason: NackReason, vararg events: DomainEvent<T>)
}
