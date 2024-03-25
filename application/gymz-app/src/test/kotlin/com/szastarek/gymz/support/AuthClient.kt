package com.szastarek.gymz.support

import com.szastarek.gymz.adapter.rest.AUTH_API_PREFIX
import com.szastarek.gymz.shared.security.Jwt
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

internal suspend fun HttpClient.me(
    authToken: Jwt,
    idToken: Jwt,
): HttpResponse = get("$AUTH_API_PREFIX/me") {
    bearerAuth(authToken.value)
    parameter("id_token", idToken.value)
}
