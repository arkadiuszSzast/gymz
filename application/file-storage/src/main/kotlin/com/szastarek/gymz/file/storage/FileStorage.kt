package com.szastarek.gymz.file.storage

import com.szastarek.gymz.file.storage.model.FileCandidate
import com.szastarek.gymz.file.storage.model.StoredFile

interface FileStorage {
    suspend fun uploadPublic(candidate: FileCandidate): StoredFile
}
