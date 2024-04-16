package com.szastarek.gymz.file.storage.model

import kotlinx.datetime.Instant

data class StoredFile(
    val key: FileKey,
    val basePath: FileBasePath,
    val fileExtension: FileExtension,
    val savedAt: Instant,
)
