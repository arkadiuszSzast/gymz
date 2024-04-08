package com.szastarek.gymz.event.store.adapter

import com.eventstore.dbclient.Position
import com.szastarek.gymz.event.store.model.Position as PositionDomain

fun Position.toDomain(): PositionDomain = PositionDomain(
    commit = this.commitUnsigned,
    prepare = this.prepareUnsigned,
)
