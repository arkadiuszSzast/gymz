package com.szastarek.gymz.support

import com.szastarek.gymz.adapter.rest.user.equipment.USER_OWNED_EQUIPMENTS_API_PREFIX
import com.szastarek.gymz.adapter.rest.user.equipment.request.ChangeUserOwnedEquipmentRequest
import com.szastarek.gymz.shared.security.Jwt
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal suspend fun HttpClient.userOwnedEquipment(
    authToken: Jwt,
): HttpResponse = get(USER_OWNED_EQUIPMENTS_API_PREFIX) {
    bearerAuth(authToken.value)
}

internal suspend fun HttpClient.changeUserOwnedEquipment(
    authToken: Jwt,
    request: ChangeUserOwnedEquipmentRequest,
): HttpResponse = post(USER_OWNED_EQUIPMENTS_API_PREFIX) {
    contentType(ContentType.Application.Json)
    bearerAuth(authToken.value)
    setBody(request)
}
