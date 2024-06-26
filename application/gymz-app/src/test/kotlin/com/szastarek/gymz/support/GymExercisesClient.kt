package com.szastarek.gymz.support

import com.szastarek.gymz.adapter.rest.exercise.GYM_EXERCISES_API_PREFIX
import com.szastarek.gymz.adapter.rest.exercise.request.AddGymExerciseRequest
import com.szastarek.gymz.domain.model.exercise.GymExerciseId
import com.szastarek.gymz.shared.page.PageQueryParameters
import com.szastarek.gymz.shared.security.Jwt
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal suspend fun HttpClient.addGymExercise(
    authToken: Jwt,
    request: AddGymExerciseRequest,
): HttpResponse = post(GYM_EXERCISES_API_PREFIX) {
    contentType(ContentType.Application.Json)
    bearerAuth(authToken.value)
    setBody(request)
}

internal suspend fun HttpClient.getAllGymExercises(
    authToken: Jwt,
    pageQueryParameters: PageQueryParameters = PageQueryParameters.default,
): HttpResponse = get(GYM_EXERCISES_API_PREFIX) {
    contentType(ContentType.Application.Json)
    bearerAuth(authToken.value)
    parameter("pageSize", pageQueryParameters.pageSize.value)
    parameter("pageNumber", pageQueryParameters.pageNumber.value)
}

internal suspend fun HttpClient.getGymExerciseById(
    authToken: Jwt,
    id: GymExerciseId,
): HttpResponse = get("GYM_EXERCISES_API_PREFIX/${id.value}") {
    contentType(ContentType.Application.Json)
    bearerAuth(authToken.value)
}
