package com.szastarek.gymz.file.storage

import com.szastarek.gymz.file.storage.model.StoredFile
import io.ktor.http.Url

fun interface PublicFileUrlResolver {
    fun resolve(file: StoredFile): Url
}
