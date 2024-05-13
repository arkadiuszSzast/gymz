package com.szastarek.gymz.adapter.rest.equipment

import com.szastarek.gymz.domain.service.equipment.query.handler.SupportedEquipmentsQuery
import com.szastarek.gymz.service.plugins.jwtAuthenticate
import com.szastarek.gymz.shared.security.userContext
import com.trendyol.kediatr.Mediator
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

const val SUPPORTED_EQUIPMENTS_API_PREFIX = "/equipments"

fun Application.supportedEquipmentsRouting() {
    val mediator by inject<Mediator>()

    routing {
        jwtAuthenticate {
            get(SUPPORTED_EQUIPMENTS_API_PREFIX) {
                val result = mediator.send(SupportedEquipmentsQuery(call.userContext))

                call.respond(HttpStatusCode.OK, result.supportedEquipments)
            }
        }
    }
}
