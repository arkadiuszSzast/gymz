package com.szastarek.gymz.shared.config

import io.ktor.server.config.ApplicationConfig

object ConfigMap {

    private lateinit var config: ApplicationConfig

    fun init(config: ApplicationConfig): ConfigMap {
        this.config = config
        return this
    }

    fun getStringProperty(key: ConfigKey) = config.property(key.key).getString()

    fun getLongProperty(key: ConfigKey) = config.property(key.key).getString().toLong()

    fun getBooleanProperty(key: ConfigKey) = config.property(key.key).getString().toBoolean()
}

@JvmInline
value class ConfigKey(val key: String) {
    operator fun plus(other: ConfigKey) = ConfigKey("$key.${other.key}")
}
