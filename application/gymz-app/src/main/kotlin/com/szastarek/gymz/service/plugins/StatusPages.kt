package com.szastarek.gymz.service.plugins

import com.szastarek.gymz.event.store.model.EventsApplyFailedException
import com.szastarek.gymz.shared.http.ProblemHttpErrorResponse
import com.szastarek.gymz.shared.http.ValidationErrorHttpMessage
import com.szastarek.gymz.shared.security.UnauthorizedException
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
        exception<UnauthorizedException> { call, ex ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ProblemHttpErrorResponse(
                    type = "unauthorized",
                    title = "Unauthorized",
                    instance = call.request.uri,
                    detail = ex.message,
                ),
            )
        }
        exception<ValidationException> { call, ex ->
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorHttpMessage(
                    validationErrors = ex.validationErrors,
                    type = ex::class.java.simpleName,
                    instance = call.request.uri,
                ),
            )
        }
        exception<EventsApplyFailedException> { call, _->
            call.respond(
                HttpStatusCode.InternalServerError,
                ProblemHttpErrorResponse(
                    type = "internal_server_error",
                    title = "Internal server error",
                    instance = call.request.uri,
                ),
            )
        }
    }
}
