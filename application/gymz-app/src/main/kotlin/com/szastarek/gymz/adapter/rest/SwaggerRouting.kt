package com.szastarek.gymz.adapter.rest

import com.szastarek.gymz.config.SwaggerProperties
import io.ktor.server.application.Application
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing

fun Application.swaggerRouting(swaggerProperties: SwaggerProperties) {
    if (swaggerProperties.enabled) {
        routing {
            swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        }
    }
}
