package com.szastarek.gymz.event.store.model

data class EventStoreWriteResult(val logPosition: Position, val nextExpectedRevision: ExpectedRevision)
