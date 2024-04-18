package com.szastarek.gymz.file.storage

import com.szastarek.gymz.file.storage.model.BucketName
import com.szastarek.gymz.file.storage.model.ResolvedBucketName

object NoOpBucketNameResolver : BucketNameResolver {
    override fun resolve(bucketName: BucketName): ResolvedBucketName {
        return ResolvedBucketName(bucketName.value)
    }
}
