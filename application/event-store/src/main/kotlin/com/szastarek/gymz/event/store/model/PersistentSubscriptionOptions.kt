package com.szastarek.gymz.event.store.model

data class PersistentSubscriptionOptions(
	val nackAction: NackAction = NackAction.Park,
	val autoAcknowledge: Boolean = true,
	val autoCreateStreamGroup: Boolean = true,
	val maxRetries: Long = 5,
)