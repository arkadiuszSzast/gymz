package com.szastarek.gymz.config

import com.szastarek.gymz.shared.config.ConfigMap
import com.typesafe.config.ConfigFactory
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.config.HoconApplicationConfig
import org.koin.test.KoinTest

class MonitoringPropertiesTest : KoinTest, StringSpec() {

    init {

        "should pick correct values from application.conf" {
            // arrange
            val config = ConfigMap.init(HoconApplicationConfig(ConfigFactory.load()))

            val expected =
                MonitoringProperties(
                    enabled = true,
                    otelMetricsUrl = "http://test-host:4318/v1/metrics",
                )

            // act & assert
            MonitoringProperties.create(config) shouldBe expected
        }
    }
}
