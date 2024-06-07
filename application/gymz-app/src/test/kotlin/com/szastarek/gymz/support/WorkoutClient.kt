package com.szastarek.gymz.support

import com.szastarek.gymz.adapter.rest.workout.WORKOUTS_API_PREFIX
import com.szastarek.gymz.adapter.rest.workout.request.AddWeeklyWorkoutTemplateRequest
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplateId
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

internal suspend fun HttpClient.addWeeklyWorkoutTemplate(
    authToken: Jwt,
    request: AddWeeklyWorkoutTemplateRequest,
): HttpResponse = post(WORKOUTS_API_PREFIX) {
    contentType(ContentType.Application.Json)
    bearerAuth(authToken.value)
    setBody(request)
}

internal suspend fun HttpClient.getAllWeeklyWorkouts(
    authToken: Jwt,
    pageQueryParameters: PageQueryParameters = PageQueryParameters.default,
): HttpResponse = get(WORKOUTS_API_PREFIX) {
    contentType(ContentType.Application.Json)
    bearerAuth(authToken.value)
    parameter("pageSize", pageQueryParameters.pageSize.value)
    parameter("pageNumber", pageQueryParameters.pageNumber.value)
}

internal suspend fun HttpClient.getWeeklyWorkoutById(
    authToken: Jwt,
    id: WeeklyWorkoutTemplateId,
): HttpResponse = get("$WORKOUTS_API_PREFIX/$id") {
    contentType(ContentType.Application.Json)
    bearerAuth(authToken.value)
}
