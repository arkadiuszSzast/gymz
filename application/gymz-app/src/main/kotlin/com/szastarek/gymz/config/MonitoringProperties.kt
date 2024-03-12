package com.szastarek.gymz.config

data class MonitoringProperties(
    val enabled: Boolean,
    val otelMetricsUrl: String,
)
