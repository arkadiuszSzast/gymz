package com.szastarek.gymz.file.storage

import com.szastarek.gymz.file.storage.model.BucketName
import com.szastarek.gymz.file.storage.model.ResolvedBucketName

fun interface BucketNameResolver {
    fun resolve(bucketName: BucketName): ResolvedBucketName
}
