package com.szastarek.gymz.config

import com.szastarek.gymz.shared.config.ConfigMap
import com.typesafe.config.ConfigFactory
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.config.HoconApplicationConfig

class MongoPropertiesTest : StringSpec() {

    init {

        "should pick correct values from application.conf" {
            // arrange
            val config = ConfigMap.init(HoconApplicationConfig(ConfigFactory.load()))

            val expected =
                MongoProperties(
                    connectionString = "mongodb://test-mongo-host:27017",
                    database = "test-mongo-database",
                )

            // act & assert
            MongoProperties.create(config) shouldBe expected
        }
    }
}
