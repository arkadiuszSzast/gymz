package com.szastarek.gymz.file.storage.config

import com.szastarek.gymz.shared.config.ConfigMap
import com.typesafe.config.ConfigFactory
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.config.HoconApplicationConfig

class S3PropertiesTest : StringSpec({

    "should pick correct values from application.conf" {
        // arrange
        val config = ConfigMap.init(HoconApplicationConfig(ConfigFactory.load()))

        val expected = S3Properties(
            s3Endpoint = "http://test-s3:1234",
            bucketPrefix = "test",
            region = "us-east-1",
            useLocalstackCredentialsProvider = true,
        )

        // act & assert
        S3Properties.create(config) shouldBe expected
    }
})
