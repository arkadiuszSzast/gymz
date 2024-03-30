package com.szastarek.gymz.event.store.config

data class EventStoreProperties(
	val connectionString: String,
	val reSubscribeOnDrop: Boolean,
)
