package com.szastarek.gymz.domain.service.upload.command.handler

import arrow.core.Either
import arrow.core.nel
import com.szastarek.gymz.domain.service.upload.command.UploadCommand
import com.szastarek.gymz.domain.service.upload.command.UploadCommandError
import com.szastarek.gymz.domain.service.upload.command.UploadCommandResult
import com.szastarek.gymz.domain.service.upload.command.UploadCommandSuccessResult
import com.szastarek.gymz.domain.service.user.AccessManager
import com.szastarek.gymz.domain.service.user.Action
import com.szastarek.gymz.domain.service.user.ensure
import com.szastarek.gymz.file.storage.FileStorage
import com.szastarek.gymz.file.storage.FileUrlResolver
import com.szastarek.gymz.file.storage.model.FileBasePath
import com.szastarek.gymz.file.storage.model.FileCandidate
import com.szastarek.gymz.file.storage.model.FileKey
import com.trendyol.kediatr.CommandWithResultHandler
import dev.cerbos.sdk.builders.Resource
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class UploadCommandHandler(
    private val fileStorage: FileStorage,
    private val fileUrlResolver: FileUrlResolver,
    private val accessManager: AccessManager,
) : CommandWithResultHandler<UploadCommand, UploadCommandResult> {
    override suspend fun handle(command: UploadCommand): UploadCommandResult {
        val (userContext, content) = command

        accessManager.check(
            userContext,
            Resource.newInstance("uploads:object"),
            Action("upload"),
        ).ensure()

        val candidate = FileCandidate.ByteFileCandidate(
            FileBasePath("uploads"),
            FileKey.new(),
            content,
        )

        return Either.catch {
            val uploadResult = fileStorage.uploadPublic(candidate)
            val url = fileUrlResolver.resolve(uploadResult)
            UploadCommandSuccessResult(url)
        }.mapLeft {
            logger.error(it) { "Failed to upload file." }
            UploadCommandError.UnknownError.nel()
        }
    }
}
