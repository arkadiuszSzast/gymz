package com.szastarek.gymz.shared.model

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EmailAddressTest : StringSpec({

    "should create email address" {
        // arrange & act
        val email = EmailAddress("test@test.com")

        // assert
        email.shouldBeRight()
    }

    "should trim email address" {
        // arrange & act
        val email = EmailAddress("  test@test.com  ")

        // assert
        email.shouldBeRight()
            .value shouldBe "test@test.com"
    }

    "should not create email address" {
        // arrange & act
        val email = EmailAddress("test@test")

        // assert
        email.shouldBeLeft().map { it.message } shouldBe listOf("invalid_email")
    }
})
