package com.szastarek.gymz.adapter.rest

import com.szastarek.gymz.file.storage.FileStorage
import com.szastarek.gymz.file.storage.FileUrlResolver
import com.szastarek.gymz.file.storage.model.ByteFileCandidate
import com.szastarek.gymz.file.storage.model.FileBasePath
import com.szastarek.gymz.file.storage.model.FileKey
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveChannel
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.toByteArray
import org.koin.ktor.ext.inject
import java.util.UUID

fun Application.uploadsRouting() {
    val fileStorage by inject<FileStorage>()
    val fileUrlResolver by inject<FileUrlResolver>()

    routing {
        post("/upload") {
            val uploadResult = fileStorage.uploadPublic(
                ByteFileCandidate(
                    FileBasePath("uploads"),
                    FileKey(UUID.randomUUID().toString()),
                    call.receiveChannel().toByteArray(),
                ),
            )
            val url = fileUrlResolver.resolve(uploadResult)
            call.respondText("A file is uploaded to: $url")
        }
    }
}
