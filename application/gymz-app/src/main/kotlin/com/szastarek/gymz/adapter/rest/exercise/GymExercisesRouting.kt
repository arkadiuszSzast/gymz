package com.szastarek.gymz.adapter.rest.exercise

import com.szastarek.gymz.adapter.rest.exercise.request.AddGymExerciseRequest
import com.szastarek.gymz.adapter.rest.exercise.response.GymExercisePageItem
import com.szastarek.gymz.domain.service.exercise.command.AddGymExerciseCommand
import com.szastarek.gymz.domain.service.exercise.query.FindAllGymExercisesQuery
import com.szastarek.gymz.file.storage.FileUrlResolver
import com.szastarek.gymz.service.plugins.jwtAuthenticate
import com.szastarek.gymz.shared.page.getPageParameters
import com.szastarek.gymz.shared.security.userContext
import com.trendyol.kediatr.Mediator
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

const val GYM_EXERCISES_API_PREFIX = "/exercises"

fun Application.gymExercisesRouting() {
    val mediator by inject<Mediator>()
    val fileUrlResolver by inject<FileUrlResolver>()

    routing {
        jwtAuthenticate {
            post(GYM_EXERCISES_API_PREFIX) {
                val userContext = call.userContext
                val request = call.receive<AddGymExerciseRequest>()
                val command = AddGymExerciseCommand(
                    userContext = userContext,
                    name = request.name,
                    description = request.description,
                    imageUrl = Url(request.imageUrl),
                    videoUrl = Url(request.videoUrl),
                    primaryMusclesGroups = request.primaryMusclesGroups,
                    secondaryMusclesGroups = request.secondaryMusclesGroups,
                    requiredEquipmentsIds = request.requiredEquipmentsIds,
                    tags = request.tags,
                )
                val result = mediator.send(command)

                result.httpResponse(call)
            }

            get(GYM_EXERCISES_API_PREFIX) {
                val userContext = call.userContext
                val pageQueryParameters = call.getPageParameters()
                val query = FindAllGymExercisesQuery(userContext, pageQueryParameters)
                val result = mediator.send(query)
                val response = result.exercises.map { GymExercisePageItem.from(it, fileUrlResolver) }

                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}
