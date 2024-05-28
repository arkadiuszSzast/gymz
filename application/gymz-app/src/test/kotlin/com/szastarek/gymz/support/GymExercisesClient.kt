package com.szastarek.gymz.support

import com.szastarek.gymz.adapter.rest.exercise.GYM_EXERCISES_API_PREFIX
import com.szastarek.gymz.adapter.rest.exercise.request.AddGymExerciseRequest
import com.szastarek.gymz.shared.security.Jwt
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
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
