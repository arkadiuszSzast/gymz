package com.szastarek.gymz.shared.security

import arrow.core.Either
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.serialization.Serializable

sealed interface JwtError
data class JwtDecodeError(val details: String) : JwtError
data class JwtCreationError(val details: String) : JwtError
data class JwtVerificationError(val details: String) : JwtError

@JvmInline
@Serializable
value class Jwt private constructor(val value: String) {

    val decoded: DecodedJWT
        get() = JWT.decode(value)

    companion object {
        fun JWTCreator.Builder.signCatching(algorithm: Algorithm): Either<JwtCreationError, Jwt> = Either.catch {
            Jwt(sign(algorithm))
        }.mapLeft { JwtCreationError(it.message ?: "Unknown error") }

        fun fromRawString(value: String): Either<JwtDecodeError, Jwt> = Either.catch {
            Jwt(JWT.decode(value).token)
        }.mapLeft { JwtDecodeError(it.message ?: "Unknown error") }
    }

    fun verify(algorithm: Algorithm) = Either.catch {
        JWT.require(algorithm)
            .build()
            .verify(value)
    }.mapLeft { JwtVerificationError(it.message ?: "Unknown error") }
}
