package com.szastarek.gymz.config

import com.szastarek.gymz.plugins.configurationModule
import io.kotest.core.spec.style.StringSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import org.koin.test.KoinTest
import org.koin.test.inject

class MonitoringPropertiesTest : KoinTest, StringSpec() {
    private val monitoringProperties by inject<MonitoringProperties>()

    init {

        extensions(KoinExtension(configurationModule))

        "should pick correct values from application.conf" {
            // arrange
            val expected =
                MonitoringProperties(
                    enabled = true,
                    otelMetricsUrl = "http://test-host:4318/v1/metrics",
                )

            // act & assert
            monitoringProperties shouldBe expected
        }
    }
}
