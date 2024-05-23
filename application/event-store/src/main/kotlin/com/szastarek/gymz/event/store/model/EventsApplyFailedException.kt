package com.szastarek.gymz.event.store.model

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right

data class EventsApplyFailedException(val reason: String) :
    RuntimeException("Failed to apply events because of: $reason")

fun <T> Either<EventsApplyFailedException, T>.getOrThrow(): T {
    return when (this) {
        is Left -> throw this.value
        is Right -> this.value
    }
}
