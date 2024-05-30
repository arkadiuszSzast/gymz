package com.szastarek.gymz.domain.model.tag

import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.szastarek.gymz.shared.validation.ValidationError
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Tag private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String) = either {
            val trimmed = value.trim()
            ensure(trimmed.isNotBlank()) { ValidationError(".tag", "blank_tag").nel() }

            Tag(trimmed)
        }
    }
}
