package com.szastarek.gymz.shared.security

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MaskedStringTest : StringSpec({

    "should hide value when toString is called" {
        // arrange
        val maskedString = MaskedString("test-secret")

        // act
        val result = maskedString.toString()

        // assert
        result shouldBe "*masked*"
    }
})
