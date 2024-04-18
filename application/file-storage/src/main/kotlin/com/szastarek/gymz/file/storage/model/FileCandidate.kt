package com.szastarek.gymz.file.storage.model

import io.ktor.http.Url

sealed interface FileCandidate {
    val basePath: FileBasePath
    val key: FileKey

    data class ByteFileCandidate(
        override val basePath: FileBasePath,
        override val key: FileKey,
        val content: ByteArray,
    ) : FileCandidate

    data class ExternalUrlFileCandidate(
        override val basePath: FileBasePath,
        override val key: FileKey,
        val sourceUrl: Url,
    ) : FileCandidate
}
