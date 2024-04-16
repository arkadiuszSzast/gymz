package com.szastarek.gymz.file.storage

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.content.toByteArray
import aws.smithy.kotlin.runtime.net.url.Url
import com.szastarek.gymz.file.storage.config.S3Properties
import com.szastarek.gymz.file.storage.model.BucketName
import com.szastarek.gymz.file.storage.model.ByteFileCandidate
import com.szastarek.gymz.file.storage.model.ExternalUrlFileCandidate
import com.szastarek.gymz.file.storage.model.FileBasePath
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
    private val httpClient = HttpClient(mockEngine)

    private val s3Client = S3Client {
        forcePathStyle = true
        endpointUrl = Url.parse(LocalstackContainer.s3Endpoint)
        region = LocalstackContainer.region
        credentialsProvider =
            StaticCredentialsProvider(Credentials(LocalstackContainer.accessKey, LocalstackContainer.secretKey))
    }
    private val s3Properties = S3Properties(LocalstackContainer.s3Endpoint, "test", LocalstackContainer.region)
    private val bucketNameResolver = PrefixBucketNameResolver(s3Properties)
    private val s3FileStorage = S3FileStorage(
        httpClient,
        s3Client,
        bucketNameResolver,
        clock,
    )

    init {
        beforeTest {
            s3Client.removeAllBuckets()
            s3Client.createBucket(bucketNameResolver, bucketName)
        }

        "should upload content file" {
            // arrange
            val key = FileKey(UUID.randomUUID().toString())
            val fileCandidate = ByteFileCandidate(
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
            val fileCandidate = ExternalUrlFileCandidate(
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
