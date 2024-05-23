package com.szastarek.gymz.adapter.rest.user.equipment

import com.szastarek.gymz.adapter.rest.user.equipment.request.ChangeUserOwnedEquipmentRequest
import com.szastarek.gymz.adapter.rest.user.equipment.response.UserOwnedEquipmentResponse
import com.szastarek.gymz.domain.service.user.equipment.command.ChangeUserOwnedEquipmentCommand
import com.szastarek.gymz.domain.service.user.equipment.query.UserOwnedEquipmentQuery
import com.szastarek.gymz.service.plugins.jwtAuthenticate
import com.szastarek.gymz.shared.http.ProblemHttpErrorResponse
import com.szastarek.gymz.shared.security.userContext
import com.trendyol.kediatr.Mediator
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

const val USER_OWNED_EQUIPMENTS_API_PREFIX = "/user/equipments"

fun Application.userOwnedEquipmentsRouting() {
    val mediator by inject<Mediator>()

    routing {
        jwtAuthenticate {
            get(USER_OWNED_EQUIPMENTS_API_PREFIX) {
                val result = mediator.send(UserOwnedEquipmentQuery(call.userContext))
                call.respond(HttpStatusCode.OK, UserOwnedEquipmentResponse(result.equipments))
            }

            post(USER_OWNED_EQUIPMENTS_API_PREFIX) {
                val request = call.receive<ChangeUserOwnedEquipmentRequest>()
                val result = mediator.send(ChangeUserOwnedEquipmentCommand(call.userContext, request.equipmentsIds))

                result.fold(
                    { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ProblemHttpErrorResponse(
                                "bad_request",
                                "Bad request",
                                call.request.uri,
                                error.joinToString(", "),
                            ),
                        )
                    },
                    { call.respond(HttpStatusCode.OK) },
                )
            }
        }
    }
}
