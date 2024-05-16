package com.szastarek.gymz.support

import com.szastarek.gymz.adapter.rest.equipment.SUPPORTED_EQUIPMENTS_API_PREFIX
import com.szastarek.gymz.shared.security.Jwt
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

internal suspend fun HttpClient.supportedEquipments(
    authToken: Jwt,
): HttpResponse = get(SUPPORTED_EQUIPMENTS_API_PREFIX) {
    bearerAuth(authToken.value)
}
