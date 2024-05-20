package com.szastarek.gymz.file.storage.model

import kotlinx.serialization.Serializable
import java.util.UUID

@JvmInline
@Serializable
value class FileKey(val value: String) {
    companion object {
        fun new(): FileKey = FileKey(UUID.randomUUID().toString())
    }
}
