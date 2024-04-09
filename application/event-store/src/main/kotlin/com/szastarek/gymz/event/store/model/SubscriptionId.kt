package com.szastarek.gymz.event.store.model

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class SubscriptionId(val value: String)
