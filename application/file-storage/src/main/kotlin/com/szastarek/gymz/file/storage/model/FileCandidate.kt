package com.szastarek.gymz.file.storage.model

import com.szastarek.gymz.shared.acl.AclResource
import dev.cerbos.sdk.builders.Resource
import io.ktor.http.Url

sealed interface FileCandidate : AclResource {
    val basePath: FileBasePath
    val key: FileKey

    companion object {
        val resource: Resource = Resource.newInstance("uploads:object")
    }

    override val resource: Resource
        get() = Resource.newInstance("uploads:object", key.value)

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
