package com.szastarek.gymz.file.storage

import aws.sdk.kotlin.services.s3.S3Client
import com.szastarek.gymz.file.storage.model.BucketName
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase

class S3LifecycleListener(
    private val s3Client: S3Client,
    private val resolver: BucketNameResolver,
    private val bucketNames: List<BucketName>,
) : TestListener {
    override suspend fun beforeEach(testCase: TestCase) {
        super.beforeEach(testCase)
        s3Client.removeAllBuckets()
        bucketNames.forEach { bucketName ->
            s3Client.createBucket(resolver, bucketName)
        }
    }
}
