package com.szastarek.gymz.plugins

import com.szastarek.gymz.shared.http.ProblemHttpErrorResponse
import com.szastarek.gymz.shared.http.ValidationErrorHttpMessage
import com.szastarek.gymz.shared.validation.ValidationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<BadRequestException> { call, ex ->
            call.respond(
                HttpStatusCode.BadRequest,
                ProblemHttpErrorResponse(
                    "bad_request",
                    "Bad request",
                    call.request.uri,
                    ex.message,
                ),
            )
        }
        exception<ValidationException> { call, ex ->
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorHttpMessage(
                    ex.validationErrors,
                    ex::class.java.simpleName,
                    call.request.uri,
                ),
            )
        }
    }
}
