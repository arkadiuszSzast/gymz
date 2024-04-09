package com.szastarek.gymz.event.store.model

sealed interface ExpectedRevision {
    data object Any : ExpectedRevision
    data object NoStream : ExpectedRevision
    data object StreamExists : ExpectedRevision
    data class Exact(val revision: Long) : ExpectedRevision
}
