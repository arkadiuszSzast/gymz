package com.szastarek.gymz.adapter.rest.response

import com.szastarek.gymz.shared.security.BearerToken
import com.szastarek.gymz.shared.security.Jwt
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val authToken: Jwt,
    val idToken: Jwt,
    val refreshToken: BearerToken,
)
