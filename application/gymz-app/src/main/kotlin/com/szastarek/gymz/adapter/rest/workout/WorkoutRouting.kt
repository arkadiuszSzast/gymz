package com.szastarek.gymz.adapter.rest.workout

import com.szastarek.gymz.adapter.rest.workout.request.AddWeeklyWorkoutRequest
import com.szastarek.gymz.domain.service.workout.command.AddWeeklyWorkoutPlanCommandResult
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
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

const val WORKOUTS_API_PREFIX = "/workouts"

fun Application.workoutRouting() {
    val mediator by inject<Mediator>()

    routing {
        jwtAuthenticate {
            post(WORKOUTS_API_PREFIX) {
                val userContext = call.userContext
                val command = kotlin.runCatching { call.receive<AddWeeklyWorkoutRequest>().toCommand(userContext) }.onFailure { println(it) }.getOrNull()!!

                when (val result = mediator.send(command)) {
                    is AddWeeklyWorkoutPlanCommandResult.Ok -> call.respond(HttpStatusCode.Created, result.id)
                    is AddWeeklyWorkoutPlanCommandResult.UnknownError -> call.respond(
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
    }
}
