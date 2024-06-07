package com.szastarek.gymz.shared.i18n

import kotlinx.serialization.Serializable

sealed interface TranslatableValue

@JvmInline
@Serializable
value class TranslationKey(val key: String) : TranslatableValue
