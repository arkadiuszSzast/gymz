package com.szastarek.gymz.file.storage

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.createBucket
import aws.sdk.kotlin.services.s3.listObjects
import aws.sdk.kotlin.services.s3.model.DeleteBucketRequest
import aws.sdk.kotlin.services.s3.model.DeleteObjectRequest
import com.szastarek.gymz.file.storage.model.BucketName

suspend fun S3Client.removeAllBuckets() {
    val buckets = listBuckets().buckets.orEmpty()
    buckets.map { it.name }.forEach { bucketName ->
        listObjects { bucket = bucketName }.contents.orEmpty().forEach {
            deleteObject(
                DeleteObjectRequest {
                    bucket = bucketName
                    key = it.key
                },
            )
        }
        deleteBucket(DeleteBucketRequest { bucket = bucketName })
    }
}

suspend fun S3Client.createBucket(resolver: BucketNameResolver, bucketName: BucketName) {
    createBucket { bucket = resolver.resolve(bucketName).value }
}
