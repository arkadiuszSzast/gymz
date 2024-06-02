package com.szastarek.gymz.config

import com.szastarek.gymz.shared.config.ConfigMap
import com.typesafe.config.ConfigFactory
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.config.HoconApplicationConfig

class SwaggerPropertiesTest : StringSpec({

    "should pick correct values from application.conf" {
        // arrange
        val config = ConfigMap.init(HoconApplicationConfig(ConfigFactory.load()))

        val expected =
            SwaggerProperties(
                enabled = true,
            )

        // act & assert
        SwaggerProperties.create(config) shouldBe expected
    }
})
