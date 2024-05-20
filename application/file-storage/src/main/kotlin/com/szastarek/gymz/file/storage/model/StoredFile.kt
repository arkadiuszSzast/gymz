package com.szastarek.gymz.file.storage.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class StoredFile(
    val key: FileKey,
    val basePath: FileBasePath,
    val fileExtension: FileExtension,
    val savedAt: Instant,
)
