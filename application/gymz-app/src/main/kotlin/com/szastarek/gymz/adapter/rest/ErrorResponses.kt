package com.szastarek.gymz.adapter.rest

import com.szastarek.gymz.shared.http.ProblemHttpErrorResponse
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.uri

object ErrorResponses {

    fun unexpectedError(call: ApplicationCall, detail: String = "An unexpected error occurred") =
        ProblemHttpErrorResponse(
            "unexpected_error",
            "Unexpected error",
            call.request.uri,
            detail,
        )
}
