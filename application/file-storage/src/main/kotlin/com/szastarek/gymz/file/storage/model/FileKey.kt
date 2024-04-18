package com.szastarek.gymz.file.storage.model

import java.util.UUID

@JvmInline
value class FileKey(val value: String) {
    companion object {
        fun new(): FileKey = FileKey(UUID.randomUUID().toString())
    }
}
