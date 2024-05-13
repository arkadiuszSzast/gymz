package com.szastarek.gymz.adapter.rest

import com.szastarek.gymz.domain.service.upload.command.UploadCommand
import com.szastarek.gymz.service.plugins.jwtAuthenticate
import com.szastarek.gymz.shared.security.userContext
import com.trendyol.kediatr.Mediator
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveChannel
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.toByteArray
import org.koin.ktor.ext.inject

const val UPLOADS_API_PREFIX = "/uploads"

fun Application.uploadsRouting() {
    val mediator by inject<Mediator>()

    routing {
        jwtAuthenticate {
            post(UPLOADS_API_PREFIX) {
                val userContext = call.userContext
                val command = UploadCommand(userContext, call.receiveChannel().toByteArray())
                val result = mediator.send(command)

                result.fold(
                    { call.respond(HttpStatusCode.InternalServerError, ErrorResponses.unexpectedError(call)) },
                    {
                        call.response.header(HttpHeaders.Location, it.url.toString())
                        call.respond(HttpStatusCode.Accepted)
                    },
                )
            }
        }
    }
}
