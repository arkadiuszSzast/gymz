package com.szastarek.gymz.event.store.model

interface PersistentSubscription {
    val subscriptionId: SubscriptionId

    fun stop()
    fun ack(vararg events: DomainEvent)
    fun nack(nackAction: NackAction, reason: NackReason, vararg events: DomainEvent)
}