package com.szastarek.gymz.file.storage

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.ObjectCannedAcl
import aws.sdk.kotlin.services.s3.putObject
import aws.smithy.kotlin.runtime.content.ByteStream
import com.szastarek.gymz.file.storage.model.BucketName
import com.szastarek.gymz.file.storage.model.ByteFileCandidate
import com.szastarek.gymz.file.storage.model.ExternalUrlFileCandidate
import com.szastarek.gymz.file.storage.model.FileCandidate
import com.szastarek.gymz.file.storage.model.FileExtension
import com.szastarek.gymz.file.storage.model.StoredFile
import com.szastarek.gymz.file.storage.model.s3ObjectMetadata
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.datetime.Clock
import org.apache.tika.config.TikaConfig
import org.apache.tika.metadata.Metadata

class S3FileStorage(
    private val httpClient: HttpClient,
    private val s3Client: S3Client,
    private val bucketNameResolver: BucketNameResolver,
    private val clock: Clock,
) : FileStorage {
    override suspend fun uploadPublic(candidate: FileCandidate): StoredFile {
        val bucketName = bucketNameResolver.resolve(BucketName(candidate.basePath.value))

        val content = when (candidate) {
            is ByteFileCandidate -> candidate.content
            is ExternalUrlFileCandidate -> httpClient.get(candidate.sourceUrl).body()
        }

        val tikaConfig = TikaConfig.getDefaultConfig()
        val mediaType = tikaConfig.mimeRepository.detect(content.inputStream(), Metadata())
        val extension = tikaConfig.mimeRepository.forName(mediaType.toString()).extension
        val savedAt = clock.now()

        s3Client.putObject {
            bucket = bucketName.value
            key = candidate.key.value
            body = ByteStream.fromBytes(content)
            contentType = mediaType.toString()
            acl = ObjectCannedAcl.PublicRead
            metadata = s3ObjectMetadata {
                this.mediaType = mediaType
                this.extension = extension
                this.savedAt = savedAt
            }
        }

        return StoredFile(
            key = candidate.key,
            basePath = candidate.basePath,
            fileExtension = FileExtension(extension),
            savedAt = savedAt,
        )
    }
}
