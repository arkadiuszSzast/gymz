package com.szastarek.gymz.config

import com.szastarek.gymz.plugins.configurationModule
import com.szastarek.gymz.shared.security.ClientId
import com.szastarek.gymz.shared.security.MaskedString
import io.kotest.core.spec.style.StringSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.ktor.http.Url
import org.koin.test.KoinTest
import org.koin.test.inject

class ZitadelPropertiesTest : KoinTest, StringSpec() {
    private val zitadelProperties by inject<ZitadelProperties>()

    init {

        extensions(KoinExtension(configurationModule))

        "should pick correct values from application.conf" {
            // arrange

            val expected =
                ZitadelProperties(
                    authorizeUrl = Url("http://test-zitadel.com/oauth/v2/authorize"),
                    accessTokenUrl = Url("http://test-zitadel.com/oauth/v2/token"),
                    callbackUrl = Url("http://test-zitadel.com/auth/callback"),
                    clientSecret = MaskedString("test-client-secret"),
                    clientId = ClientId("test-client-id"),
                )

            // act & assert
            zitadelProperties shouldBe expected
        }
    }
}
