package com.szastarek.gymz.config

import com.szastarek.gymz.shared.config.ConfigKey
import com.szastarek.gymz.shared.config.ConfigMap

data class MongoProperties(
    val connectionString: String,
    val database: String,
) {
    companion object {
        fun create(config: ConfigMap): MongoProperties {
            return MongoProperties(
                connectionString = config.getStringProperty(ConfigKey("mongo.connectionString")),
                database = config.getStringProperty(ConfigKey("mongo.database")),
            )
        }
    }
}
