package com.szastarek.gymz.config

import com.szastarek.gymz.plugins.configurationModule
import com.szastarek.gymz.shared.security.JwtAudience
import com.szastarek.gymz.shared.security.JwtIssuer
import com.szastarek.gymz.shared.security.JwtRealm
import com.szastarek.gymz.shared.security.MaskedString
import io.kotest.core.spec.style.StringSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import org.koin.test.KoinTest
import org.koin.test.inject

class JwtAuthTokenPropertiesTest : KoinTest, StringSpec() {
    private val jwtAuthTokenProperties by inject<JwtAuthTokenProperties>()

    init {

        extensions(KoinExtension(configurationModule))

        "should pick correct values from application.conf" {
            // arrange

            val expected =
                JwtAuthTokenProperties(
                    audience = JwtAudience("test-auth-audience"),
                    issuer = JwtIssuer("test-auth-issuer"),
                    realm = JwtRealm("test-auth-realm"),
                    secret = MaskedString("test-auth-secret"),
                )

            // act & assert
            jwtAuthTokenProperties shouldBe expected
        }
    }
}
