package com.szastarek.gymz.adapter.rest.exercise

import com.szastarek.gymz.domain.service.exercise.command.AddGymExerciseCommandResult
import com.szastarek.gymz.shared.http.ProblemHttpErrorResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.uri
import io.ktor.server.response.header
import io.ktor.server.response.respond

suspend fun AddGymExerciseCommandResult.httpResponse(call: ApplicationCall) = when (this) {
    is AddGymExerciseCommandResult.AddGymExerciseSuccessResult -> {
        call.response.header(HttpHeaders.Location, "${call.request.uri}/${this.exerciseId.value}")
        call.respond(HttpStatusCode.Created)
    }

    is AddGymExerciseCommandResult.AddGymExerciseCommandError.EquipmentNotFound -> {
        call.respond(
            HttpStatusCode.BadRequest,
            ProblemHttpErrorResponse(
                "required_equipment_not_found",
                "Required equipment not found",
                call.request.uri,
                "Equipments with ids: [${this.notFoundEquipments.joinToString(",")}] not found.",
            ),
        )
    }

    is AddGymExerciseCommandResult.AddGymExerciseCommandError.UnknownError -> {
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
