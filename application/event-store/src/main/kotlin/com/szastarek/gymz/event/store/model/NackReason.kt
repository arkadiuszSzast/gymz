package com.szastarek.gymz.event.store.model

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class NackReason(val value: String)
