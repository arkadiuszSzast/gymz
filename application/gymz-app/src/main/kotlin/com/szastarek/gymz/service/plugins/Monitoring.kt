package com.szastarek.gymz.service.plugins

import com.szastarek.gymz.config.MonitoringProperties
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.micrometer.core.instrument.Clock
import io.micrometer.registry.otlp.OtlpConfig
import io.micrometer.registry.otlp.OtlpMeterRegistry
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.instrumentation.ktor.v2_0.server.KtorServerTracing

internal fun Application.configureMonitoring(
    monitoringProperties: MonitoringProperties,
    openTelemetry: OpenTelemetry,
) {
    if (monitoringProperties.enabled) {
        val otlpConfig =
            OtlpConfig {
                when (it) {
                    "otlp.url" -> monitoringProperties.otelMetricsUrl
                    else -> null
                }
            }
        install(MicrometerMetrics) {
            registry = OtlpMeterRegistry(otlpConfig, Clock.SYSTEM)
        }
        install(KtorServerTracing) {
            setOpenTelemetry(openTelemetry)
        }
    }
}