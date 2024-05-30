package com.szastarek.gymz.domain.service.upload.command

import arrow.core.Either
import arrow.core.Nel
import com.szastarek.gymz.shared.security.UserContext
import com.trendyol.kediatr.CommandWithResult
import io.ktor.http.Url

typealias UploadCommandResult = Either<Nel<UploadCommandError>, UploadCommandSuccessResult>

data class UploadCommandSuccessResult(val url: Url)

data class UploadCommand(
    val userContext: UserContext,
    val content: ByteArray,
) : CommandWithResult<UploadCommandResult> {
    override fun toString(): String {
        return "UploadCommand(userContext=$userContext, content=**ByteArray**)"
    }
}

enum class UploadCommandError {
    UnknownError,
}
