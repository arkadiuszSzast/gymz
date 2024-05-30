package com.szastarek.gymz.domain.service.upload.command.handler

import arrow.core.nel
import com.szastarek.gymz.adapter.cerbos.CerbosAccessManager
import com.szastarek.gymz.cerbos.CerbosContainer
import com.szastarek.gymz.domain.service.upload.command.UploadCommand
import com.szastarek.gymz.file.storage.LocalstackProvider
import com.szastarek.gymz.file.storage.model.BucketName
import com.szastarek.gymz.shared.security.TestFixtures.userContext
import dev.cerbos.sdk.CerbosClientBuilder
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec

class UploadCommandHandlerTest : StringSpec({

    val fileContent = ClassLoader.getSystemResource("static-files/example-text.txt").readBytes()
    val accessManager = CerbosAccessManager(CerbosClientBuilder(CerbosContainer.url).withPlaintext().buildBlockingClient())
    val localstackProvider = LocalstackProvider()
    val fileStorage = localstackProvider.s3FileStorage
    val fileUrlResolver = localstackProvider.fileUrlResolver

    val handler = UploadCommandHandler(fileStorage, fileUrlResolver, accessManager)

    listener(localstackProvider.s3LifecycleListener(BucketName("uploads").nel()))

    "should upload file" {
        // arrange
        val userContext = userContext()
        val command = UploadCommand(userContext, fileContent)

        // act
        val result = handler.handle(command)

        // assert
        result.shouldBeRight()
    }
})
