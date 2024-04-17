package com.szastarek.gymz.file.storage

import com.szastarek.gymz.file.storage.config.S3Properties
import com.szastarek.gymz.file.storage.model.FileBasePath
import com.szastarek.gymz.file.storage.model.FileExtension
import com.szastarek.gymz.file.storage.model.FileKey
import com.szastarek.gymz.file.storage.model.StoredFile
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant

class S3FileUrlResolverTest : StringSpec({

    val s3Properties = S3Properties(
        s3Endpoint = "http://localhost:4566",
        bucketPrefix = "s3-file-url-resolver-test",
        region = "us-east-1",
    )
    val fileUrlResolver = S3FileUrlResolver(PrefixBucketNameResolver(s3Properties), s3Properties)

    "should resolve file url" {
        // arrange
        val storedFile = StoredFile(
            basePath = FileBasePath("images"),
            key = FileKey("resolve-file-url-test"),
            fileExtension = FileExtension("png"),
            savedAt = Instant.parse("2021-01-01T00:00:00Z"),
        )

        // act
        val result = fileUrlResolver.resolve(storedFile).toString()

        // assert
        result shouldBe "${s3Properties.s3Endpoint}/${s3Properties.bucketPrefix}-${storedFile.basePath.value}/${storedFile.key.value}"
    }
})
