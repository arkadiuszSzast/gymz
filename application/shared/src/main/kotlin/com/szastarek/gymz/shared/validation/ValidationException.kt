package com.szastarek.gymz.shared.validation

import arrow.core.Either
import arrow.core.nel

data class ValidationException(val validationErrors: List<ValidationError>) :
    RuntimeException(validationErrors.joinToString(","))

@JvmName("getOrThrowValidationErrors")
fun <T> Either<ValidationErrors, T>.getOrThrow(): T {
    return when (this) {
        is Either.Left -> throw ValidationException(this.value)
        is Either.Right -> this.value
    }
}

@JvmName("getOrThrowValidationError")
fun <T> Either<ValidationError, T>.getOrThrow(): T {
    return when (this) {
        is Either.Left -> throw ValidationException(this.value.nel())
        is Either.Right -> this.value
    }
}
