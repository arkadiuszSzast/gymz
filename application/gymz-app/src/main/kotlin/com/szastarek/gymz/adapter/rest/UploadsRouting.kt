package com.szastarek.gymz.adapter.rest

import com.szastarek.gymz.file.storage.FileStorage
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

fun Application.uploadsRouting() {
    val fileStorage by inject<FileStorage>()

    routing {
        post("/upload") {
            fileStorage.uploadPublic(ByteFileCandidate(FileBasePath("uploads"), FileKey("ktor_logo.png"), call.receiveChannel().toByteArray()))
            call.respondText("A file is uploaded")
        }
    }
}
