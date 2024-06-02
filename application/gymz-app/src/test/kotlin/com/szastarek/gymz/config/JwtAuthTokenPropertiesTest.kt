package com.szastarek.gymz.config

import com.szastarek.gymz.shared.config.ConfigMap
import com.szastarek.gymz.shared.security.JwtAudience
import com.szastarek.gymz.shared.security.JwtIssuer
import com.szastarek.gymz.shared.security.JwtRealm
import com.szastarek.gymz.shared.security.MaskedString
import com.typesafe.config.ConfigFactory
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.config.HoconApplicationConfig

class JwtAuthTokenPropertiesTest : StringSpec({

    "should pick correct values from application.conf" {
        // arrange
        val config = ConfigMap.init(HoconApplicationConfig(ConfigFactory.load()))

        val expected =
            JwtAuthTokenProperties(
                audience = JwtAudience("test-auth-audience"),
                issuer = JwtIssuer("test-auth-issuer"),
                realm = JwtRealm("test-auth-realm"),
                secret = MaskedString("test-auth-secret"),
            )

        // act & assert
        JwtAuthTokenProperties.create(config) shouldBe expected
    }
})
