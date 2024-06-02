package com.szastarek.gymz.generators

import com.szastarek.gymz.file.storage.model.FileBasePath
import com.szastarek.gymz.file.storage.model.FileExtension
import com.szastarek.gymz.file.storage.model.FileKey
import com.szastarek.gymz.file.storage.model.StoredFile
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string

object StoredFileGenerator {

    val image = arbitrary { randomSource ->
        StoredFile(
            key = FileKey.new(),
            basePath = FileBasePath(Arb.Companion.string(3..15, Codepoint.az()).single(randomSource)),
            fileExtension = Arb.of(".png", ".jpg", ".jpeg", ".svg").map { FileExtension(it) }.single(randomSource),
            savedAt = InstantGenerator.instant().single(randomSource),
        )
    }

    val video = arbitrary { randomSource ->
        StoredFile(
            key = FileKey.new(),
            basePath = FileBasePath(Arb.Companion.string(3..15, Codepoint.az()).single(randomSource)),
            fileExtension = Arb.of(".mp4", ".avi", ".wmv").map { FileExtension(it) }.single(randomSource),
            savedAt = InstantGenerator.instant().single(randomSource),
        )
    }
}
