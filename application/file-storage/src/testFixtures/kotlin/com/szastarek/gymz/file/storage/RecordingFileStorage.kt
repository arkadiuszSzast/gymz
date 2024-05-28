package com.szastarek.gymz.file.storage

import com.szastarek.gymz.file.storage.model.FileCandidate
import com.szastarek.gymz.file.storage.model.FileExtension
import com.szastarek.gymz.file.storage.model.StoredFile
import com.szastarek.gymz.utils.FixedClock
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

class RecordingFileStorage(
    private val clock: Clock = FixedClock(),
    private val fileExtensionMapper: (FileCandidate) -> FileExtension = ::defaultFileExtensionMapper,
) : FileStorage {
    private val storedFiles = mutableMapOf<FileCandidate, StoredFile>()
    private val mutex = Mutex()

    override suspend fun uploadPublic(candidate: FileCandidate): StoredFile = mutex.withLock {
        StoredFile(
            key = candidate.key,
            basePath = candidate.basePath,
            fileExtension = fileExtensionMapper(candidate),
            savedAt = clock.now(),
        ).also {
            storedFiles[candidate] = it
        }
    }
}

fun defaultFileExtensionMapper(fileCandidate: FileCandidate): FileExtension {
    return FileExtension("png")
}
