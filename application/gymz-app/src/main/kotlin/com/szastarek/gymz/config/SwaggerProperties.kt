package com.szastarek.gymz.config

import com.szastarek.gymz.shared.config.ConfigKey
import com.szastarek.gymz.shared.config.ConfigMap

data class SwaggerProperties(val enabled: Boolean) {
    companion object {
        fun create(config: ConfigMap): SwaggerProperties = SwaggerProperties(
            enabled = config.getBooleanProperty(ConfigKey("swagger.enabled")),
        )
    }
}
