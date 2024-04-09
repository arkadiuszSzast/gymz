package com.szastarek.gymz.event.store.model

fun interface ActivePersistentSubscription {
    fun stop()
}
