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

class JwtIdTokenPropertiesTest : KoinTest, StringSpec() {
    private val jwtIdTokenProperties by inject<JwtIdTokenProperties>()

    init {

        extensions(KoinExtension(configurationModule))

        "should pick correct values from application.conf" {
            // arrange

            val expected =
                JwtIdTokenProperties(
                    audience = JwtAudience("test-id-audience"),
                    issuer = JwtIssuer("test-id-issuer"),
                    realm = JwtRealm("test-id-realm"),
                    secret = MaskedString("test-id-secret"),
                )

            // act & assert
            jwtIdTokenProperties shouldBe expected
        }
    }
}
