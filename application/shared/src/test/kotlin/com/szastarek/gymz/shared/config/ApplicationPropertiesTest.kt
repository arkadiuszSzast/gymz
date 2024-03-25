package com.szastarek.gymz.shared.config

import com.szastarek.gymz.shared.config.ConfigMap.getBooleanProperty
import com.szastarek.gymz.shared.config.ConfigMap.getLongProperty
import com.szastarek.gymz.shared.config.ConfigMap.getStringProperty
import com.typesafe.config.ConfigFactory
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.config.HoconApplicationConfig

class ApplicationPropertiesTest : StringSpec({

    beforeSpec {
        ConfigMap.init(HoconApplicationConfig(ConfigFactory.load()))
    }

    "should read string values from application.conf" {
        // arrange
        val result = getStringProperty(ConfigKey("application.test.string"))
        val expected = "test_string_value"

        // act & assert
        result shouldBe expected
    }

    "should read number values from application.conf" {
        // arrange
        val result = getLongProperty(ConfigKey("application.test.number"))
        val expected = 23

        // act & assert
        result shouldBe expected
    }

    "should read boolean values from application.conf" {
        // arrange
        val result = getBooleanProperty(ConfigKey("application.test.boolean"))
        val expected = true

        // act & assert
        result shouldBe expected
    }
})
