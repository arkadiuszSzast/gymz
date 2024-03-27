package com.szastarek.gymz.shared.security

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.szastarek.gymz.shared.validation.ValidationError
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class UserId private constructor(val value: String) {

    companion object {
        operator fun invoke(id: String) = either {
            ensure(id.isNotBlank()) { ValidationError(".userId", "validation.invalid_user_id").nel() }

            UserId(id)
        }
    }
}
