package com.szastarek.gymz.support

import com.szastarek.gymz.adapter.rest.UPLOADS_API_PREFIX
import com.szastarek.gymz.shared.security.Jwt
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

internal suspend fun HttpClient.upload(
    authToken: Jwt,
    fileContent: ByteArray,
): HttpResponse = post(UPLOADS_API_PREFIX) {
    bearerAuth(authToken.value)
    setBody(fileContent)
}
