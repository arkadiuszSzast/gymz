package com.szastarek.gymz.file.storage

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.net.url.Url
import com.szastarek.gymz.file.storage.config.S3Properties
import com.szastarek.gymz.file.storage.model.BucketName
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.Clock

private val defaultMockEngine
    get() = MockEngine { request ->
        when (request.url.toString()) {
            else -> respondError(HttpStatusCode.NotFound, "Not found")
        }
    }

class LocalstackProvider(
    httpClient: HttpClient = HttpClient(defaultMockEngine),
    clock: Clock = Clock.System,
    val bucketNameResolver: BucketNameResolver = NoOpBucketNameResolver,
) {
    private val properties = S3Properties(LocalstackContainer.s3Endpoint, "", LocalstackContainer.region)

    val client: S3Client = S3Client {
        forcePathStyle = true
        endpointUrl = Url.parse(LocalstackContainer.s3Endpoint)
        region = LocalstackContainer.region
        credentialsProvider =
            StaticCredentialsProvider(Credentials(LocalstackContainer.accessKey, LocalstackContainer.secretKey))
    }

    val s3FileStorage = S3FileStorage(httpClient, client, bucketNameResolver, clock)

    val fileUrlResolver = S3FileUrlResolver(bucketNameResolver, properties)

    fun s3LifecycleListener(buckets: List<BucketName>) = S3LifecycleListener(client, bucketNameResolver, buckets)
}
