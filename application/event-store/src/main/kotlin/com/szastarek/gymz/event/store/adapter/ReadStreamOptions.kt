package com.szastarek.gymz.event.store.adapter

import com.eventstore.dbclient.Direction
import com.eventstore.dbclient.ReadStreamOptions
import com.szastarek.gymz.event.store.model.Direction as DirectionDomain
import com.szastarek.gymz.event.store.model.ReadStreamOptions as ReadStreamOptionsDomain

fun ReadStreamOptionsDomain.toEventStoreDb(): ReadStreamOptions {
    val domain = this
    val direction = when (domain.direction) {
        DirectionDomain.Forwards-> Direction.Forwards
        DirectionDomain.Backwards -> Direction.Backwards
    }
    return ReadStreamOptions.get().apply {
        this.maxCount(domain.maxCount)
        this.direction(direction)
        this.resolveLinkTos(domain.shouldResolveLinkTos)
    }
}