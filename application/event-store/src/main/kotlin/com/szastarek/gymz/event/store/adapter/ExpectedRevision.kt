package com.szastarek.gymz.event.store.adapter

import com.eventstore.dbclient.ExpectedRevision
import com.szastarek.gymz.event.store.model.ExpectedRevision as ExpectedRevisionDomain

fun ExpectedRevision.toDomain() = when (this) {
    is ExpectedRevision.AnyExpectedRevision -> ExpectedRevisionDomain.Any
    is ExpectedRevision.NoStreamExpectedRevision -> ExpectedRevisionDomain.NoStream
    is ExpectedRevision.StreamExistsExpectedRevision -> ExpectedRevisionDomain.StreamExists
    is ExpectedRevision.SpecificExpectedRevision -> ExpectedRevisionDomain.Exact(this.toRawLong())
    else -> ExpectedRevisionDomain.Any
}

fun ExpectedRevisionDomain.toEventStoreDb(): ExpectedRevision = when (this) {
    is ExpectedRevisionDomain.Any -> ExpectedRevision.any()
    is ExpectedRevisionDomain.NoStream -> ExpectedRevision.noStream()
    is ExpectedRevisionDomain.StreamExists -> ExpectedRevision.streamExists()
    is ExpectedRevisionDomain.Exact -> ExpectedRevision.expectedRevision(this.revision)
}
