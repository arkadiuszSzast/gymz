package com.szastarek.gymz.shared.model

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.szastarek.gymz.shared.validation.ValidationError
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class EmailAddress private constructor(val value: String) {
    companion object {
        operator fun invoke(address: String) =
            either {
                val trimmed = address.trim()
                ensure(trimmed.matches(Regex(EMAIL_PATTERN))) { ValidationError(".email", "invalid_email").nel() }

                EmailAddress(trimmed)
            }
    }
}

private const val EMAIL_PATTERN = """.+@.+\..+"""
