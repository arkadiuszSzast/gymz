package com.szastarek.gymz.adapter.rest.workout

import com.szastarek.gymz.adapter.rest.workout.request.AddWeeklyWorkoutTemplateRequest
import com.szastarek.gymz.adapter.rest.workout.response.WeeklyWorkoutTemplatePageItem
import com.szastarek.gymz.adapter.rest.workout.response.WeeklyWorkoutTemplateResponse
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplateId
import com.szastarek.gymz.domain.service.workout.command.AddWeeklyWorkoutTemplateCommandResult
import com.szastarek.gymz.domain.service.workout.query.FindAllWeeklyWorkoutTemplatesQuery
import com.szastarek.gymz.domain.service.workout.query.FindWeeklyWorkoutTemplateByIdQuery
import com.szastarek.gymz.domain.service.workout.query.FindWeeklyWorkoutTemplateByIdQueryResult
import com.szastarek.gymz.file.storage.FileUrlResolver
import com.szastarek.gymz.service.plugins.jwtAuthenticate
import com.szastarek.gymz.shared.http.ProblemHttpErrorResponse
import com.szastarek.gymz.shared.page.getPageParameters
import com.szastarek.gymz.shared.security.userContext
import com.trendyol.kediatr.Mediator
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

const val WORKOUTS_API_PREFIX = "/workouts"

fun Application.workoutRouting() {
    val mediator by inject<Mediator>()
    val fileUrlResolver by inject<FileUrlResolver>()

    routing {
        jwtAuthenticate {
            get(WORKOUTS_API_PREFIX) {
                val userContext = call.userContext
                val pageQueryParameters = call.getPageParameters()
                val query = FindAllWeeklyWorkoutTemplatesQuery(userContext, pageQueryParameters)
                val result = mediator.send(query)
                val response = result.weeklyWorkoutTemplates.map { WeeklyWorkoutTemplatePageItem.fromDomain(it, fileUrlResolver) }

                call.respond(HttpStatusCode.OK, response)
            }

            get("$WORKOUTS_API_PREFIX/{id}") {
                val userContext = call.userContext
                val weeklyWorkoutTemplateId = WeeklyWorkoutTemplateId(call.parameters["id"]!!)
                val query = FindWeeklyWorkoutTemplateByIdQuery(userContext, weeklyWorkoutTemplateId)

                when (val result = mediator.send(query)) {
                    is FindWeeklyWorkoutTemplateByIdQueryResult.NotFound -> call.respond(HttpStatusCode.NotFound)
                    is FindWeeklyWorkoutTemplateByIdQueryResult.Found -> call.respond(HttpStatusCode.OK, WeeklyWorkoutTemplateResponse.from(result.weeklyWorkoutTemplate, fileUrlResolver))
                }
            }

            post(WORKOUTS_API_PREFIX) {
                val userContext = call.userContext
                val command = call.receive<AddWeeklyWorkoutTemplateRequest>().toCommand(userContext)
                when (val result = mediator.send(command)) {
                    is AddWeeklyWorkoutTemplateCommandResult.Ok -> {
                        call.response.header(HttpHeaders.Location, "${call.request.uri}/${result.id.value}")
                        call.respond(HttpStatusCode.Created)
                    }
                    is AddWeeklyWorkoutTemplateCommandResult.GymExerciseNotFound -> call.respond(
                        HttpStatusCode.BadRequest,
                        ProblemHttpErrorResponse(
                            type = "exercises_not_found",
                            title = "Exercises with ids: ${result.exercisesIds} not found.",
                            instance = call.request.uri,
                        ),
                    )
                    is AddWeeklyWorkoutTemplateCommandResult.UnknownError -> call.respond(
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
