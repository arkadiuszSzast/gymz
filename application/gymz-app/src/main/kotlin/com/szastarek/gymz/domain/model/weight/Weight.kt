package com.szastarek.gymz.domain.model.weight

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Weight(val grams: ULong)
