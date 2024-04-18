package com.szastarek.gymz.file.storage

import arrow.core.nel
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.smithy.kotlin.runtime.content.toByteArray
import com.szastarek.gymz.file.storage.model.BucketName
import com.szastarek.gymz.file.storage.model.FileBasePath
import com.szastarek.gymz.file.storage.model.FileCandidate
import com.szastarek.gymz.file.storage.model.FileKey
import com.szastarek.gymz.file.storage.model.S3ObjectMetadata
import com.szastarek.gymz.utils.FixedClock
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import java.util.UUID
import io.ktor.http.Url as KtorHttpUrl

class S3FileStorageTest : StringSpec() {

    private val fileContent = ClassLoader.getSystemResource("example-text.txt").readBytes()
    private val bucketName = BucketName("s3-file-storage-test")
    private val clock = FixedClock()
    private val mockEngine = MockEngine { request ->
        when (request.url.toString()) {
            "https://example-text.test/" -> respond(fileContent)
            else -> respondError(HttpStatusCode.NotFound, "Not found")
        }
    }
    private val localstackProvider = LocalstackProvider(httpClient = HttpClient(mockEngine), clock = clock)

    private val s3Client = localstackProvider.client
    private val bucketNameResolver = localstackProvider.bucketNameResolver
    private val s3FileStorage = localstackProvider.s3FileStorage

    init {
        listener(localstackProvider.s3LifecycleListener(bucketName.nel()))

        "should upload content file" {
            // arrange
            val key = FileKey(UUID.randomUUID().toString())
            val fileCandidate = FileCandidate.ByteFileCandidate(
                basePath = FileBasePath(bucketName.value),
                key = key,
                content = fileContent,
            )

            // act
            val result = s3FileStorage.uploadPublic(fileCandidate)

            // assert
            s3Client.getObject(
                GetObjectRequest {
                    this.bucket = bucketNameResolver.resolve(bucketName).value
                    this.key = result.key.value
                },
            ) {
                val savedFileContent = it.body!!.toByteArray()
                it.contentType shouldBe "text/plain"
                savedFileContent shouldBe fileContent
                S3ObjectMetadata.create(it.metadata!!) shouldBe mapOf(
                    S3ObjectMetadata.Keys.mediaType to "text/plain",
                    S3ObjectMetadata.Keys.extension to ".txt",
                    S3ObjectMetadata.Keys.savedAt to clock.now().toString(),
                )
            }
        }

        "should upload file from external URL" {
            // arrange
            val key = FileKey(UUID.randomUUID().toString())
            val fileCandidate = FileCandidate.ExternalUrlFileCandidate(
                basePath = FileBasePath(bucketName.value),
                key = key,
                sourceUrl = KtorHttpUrl("https://example-text.test/"),
            )

            // act
            val result = s3FileStorage.uploadPublic(fileCandidate)

            // assert
            s3Client.getObject(
                GetObjectRequest {
                    this.bucket = bucketNameResolver.resolve(bucketName).value
                    this.key = result.key.value
                },
            ) {
                val savedFileContent = it.body!!.toByteArray()
                it.contentType shouldBe "text/plain"
                savedFileContent shouldBe fileContent
                S3ObjectMetadata.create(it.metadata!!) shouldBe mapOf(
                    S3ObjectMetadata.Keys.mediaType to "text/plain",
                    S3ObjectMetadata.Keys.extension to ".txt",
                    S3ObjectMetadata.Keys.savedAt to clock.now().toString(),
                )
            }
        }
    }
}
