package com.szastarek.gymz.config

import com.szastarek.gymz.shared.config.ConfigKey
import com.szastarek.gymz.shared.config.ConfigMap

data class MonitoringProperties(
    val enabled: Boolean,
    val otelMetricsUrl: String,
) {
    companion object {
        fun create(config: ConfigMap): MonitoringProperties = MonitoringProperties(
            enabled = config.getBooleanProperty(ConfigKey("monitoring.enabled")),
            otelMetricsUrl = config.getStringProperty(ConfigKey("monitoring.otel.metrics.url")),
        )
    }
}
