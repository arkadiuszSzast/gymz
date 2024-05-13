package com.szastarek.gymz.file.storage.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.apache.tika.mime.MediaType

class S3ObjectMetadataKtTest : StringSpec({

    "should build metadata" {
        // arrange && act
        val result = s3ObjectMetadata {
            mediaType = MediaType("application", "json")
            extension = "json"
        }

        // assert
        result shouldBe mapOf(
            S3ObjectMetadata.Keys.mediaType to "application/json",
            S3ObjectMetadata.Keys.extension to "json",
        )
    }
})
