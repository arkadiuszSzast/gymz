package com.szastarek.gymz.file.storage.model

import com.szastarek.gymz.shared.collection.filterNotNullValues
import kotlinx.datetime.Instant
import org.apache.tika.mime.MediaType

interface S3ObjectMetadata : Map<String, String> {

    companion object {
        fun create(data: Map<String, String>): S3ObjectMetadata = Simple(data)
    }

    // Should be defined in lower case since it's stored in lower case in S3
    object Keys {
        val savedAt = "saved-at"
        val mediaType = "media-type"
        val extension = "extension"
    }
}

private class Simple(private val data: Map<String, String>) : Map<String, String> by data, S3ObjectMetadata

fun s3ObjectMetadata(block: S3ObjectMetadataBuilder.() -> Unit): S3ObjectMetadata =
    S3ObjectMetadata.create(S3ObjectMetadataBuilder().apply(block).build())

class S3ObjectMetadataBuilder {
    var savedAt: Instant? = null
    var mediaType: MediaType? = null
    var extension: String? = null

    fun build(): S3ObjectMetadata {
        val values = mapOf(
            S3ObjectMetadata.Keys.savedAt to savedAt?.toString(),
            S3ObjectMetadata.Keys.mediaType to mediaType?.toString(),
            S3ObjectMetadata.Keys.extension to extension,
        ).filterNotNullValues()

        return Simple(values)
    }
}
