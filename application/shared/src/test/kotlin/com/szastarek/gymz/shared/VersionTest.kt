package com.szastarek.gymz.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class VersionTest : StringSpec({

    "initial version should be 0" {
        // arrange & act
        val result = Version.initial

        // assert
        result shouldBe Version(0)
    }

    "should generate next version" {
        // arrange
        val version = Version(1)

        // act
        val result = version.next()

        // assert
        result shouldBe Version(2)
    }

    "should throw exception when version is less than 0" {
        // arrange & act & assert
        shouldThrow<IllegalArgumentException> {
            Version(-1)
        }
    }
})
