package com.szastarek.gymz.config

import com.szastarek.gymz.shared.config.ConfigMap
import com.szastarek.gymz.shared.security.ClientId
import com.szastarek.gymz.shared.security.MaskedString
import com.typesafe.config.ConfigFactory
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.Url
import io.ktor.server.config.HoconApplicationConfig

class ZitadelPropertiesTest : StringSpec() {

    init {

        "should pick correct values from application.conf" {
            // arrange
            val config = ConfigMap.init(HoconApplicationConfig(ConfigFactory.load()))

            val expected =
                ZitadelProperties(
                    authorizeUrl = Url("http://test-zitadel.com/oauth/v2/authorize"),
                    accessTokenUrl = Url("http://test-zitadel.com/oauth/v2/token"),
                    callbackUrl = Url("http://test-zitadel.com/auth/callback"),
                    clientSecret = MaskedString("test-client-secret"),
                    clientId = ClientId("test-client-id"),
                )

            // act & assert
            ZitadelProperties.create(config) shouldBe expected
        }
    }
}
