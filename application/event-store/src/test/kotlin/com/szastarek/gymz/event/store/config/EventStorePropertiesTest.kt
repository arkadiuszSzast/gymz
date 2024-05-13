package com.szastarek.gymz.event.store.config

import com.szastarek.gymz.shared.config.ConfigMap
import com.typesafe.config.ConfigFactory
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.config.HoconApplicationConfig

class EventStorePropertiesTest : StringSpec({

    "should pick correct values from application.conf" {
        // arrange
        val config = ConfigMap.init(HoconApplicationConfig(ConfigFactory.load()))

        val expected = EventStoreProperties(
            connectionString = "esdb://test-host:2113?tls=false",
        )

        // act & assert
        EventStoreProperties.create(config) shouldBe expected
    }
})
