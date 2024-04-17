package com.szastarek.gymz.file.storage

import com.szastarek.gymz.file.storage.config.S3Properties
import com.szastarek.gymz.file.storage.model.BucketName
import com.szastarek.gymz.file.storage.model.StoredFile
import io.ktor.http.Url

class S3FileUrlResolver(
    private val bucketNameResolver: BucketNameResolver,
    private val s3Properties: S3Properties,
) : FileUrlResolver {
    override fun resolve(file: StoredFile): Url {
        val resolvedBucketName = bucketNameResolver.resolve(BucketName(file.basePath.value))
        return Url("${s3Properties.s3Endpoint}/${resolvedBucketName.value}/${file.key.value}")
    }
}
