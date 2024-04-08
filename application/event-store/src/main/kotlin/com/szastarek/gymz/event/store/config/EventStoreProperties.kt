package com.szastarek.gymz.event.store.config

import com.szastarek.gymz.shared.config.ConfigKey
import com.szastarek.gymz.shared.config.ConfigMap

data class EventStoreProperties(
    val connectionString: String,
) {
    companion object {
        fun create(config: ConfigMap): EventStoreProperties {
            return EventStoreProperties(config.getStringProperty(ConfigKey("eventStore.connectionString")))
        }
    }
}
