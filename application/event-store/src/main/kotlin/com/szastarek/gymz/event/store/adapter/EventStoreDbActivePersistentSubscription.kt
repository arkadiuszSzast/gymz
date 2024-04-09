package com.szastarek.gymz.event.store.adapter

import com.eventstore.dbclient.PersistentSubscription
import com.szastarek.gymz.event.store.model.ActivePersistentSubscription

class EventStoreDbActivePersistentSubscription(
    private val persistentSubscription: PersistentSubscription,
) : ActivePersistentSubscription {
    override fun stop() {
        persistentSubscription.stop()
    }
}
