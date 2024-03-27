package com.szastarek.gymz.config

import com.szastarek.gymz.shared.config.ConfigKey
import com.szastarek.gymz.shared.config.ConfigMap

data class CerbosProperties(val connectionString: String) {
    companion object {
        fun create(config: ConfigMap): CerbosProperties = CerbosProperties(
            connectionString = config.getStringProperty(ConfigKey("cerbos.connectionString")),
        )
    }
}
