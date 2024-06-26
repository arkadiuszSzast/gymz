package com.szastarek.gymz.file.storage

import com.szastarek.gymz.file.storage.model.BucketName
import com.szastarek.gymz.file.storage.model.ResolvedBucketName
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class PrefixBucketNameResolver(private val prefix: String) : BucketNameResolver {

    override fun resolve(bucketName: BucketName): ResolvedBucketName {
        val resolved = "$prefix-${bucketName.value}"
        logger.debug { "Bucket name: ${bucketName.value} resolved to: $resolved" }
        return ResolvedBucketName(resolved)
    }
}
