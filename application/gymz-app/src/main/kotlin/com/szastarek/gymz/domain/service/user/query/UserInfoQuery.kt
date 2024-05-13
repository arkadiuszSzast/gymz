package com.szastarek.gymz.domain.service.user.query

import arrow.core.Either
import arrow.core.Nel
import arrow.core.raise.either
import com.szastarek.gymz.domain.model.user.UserInfo
import com.szastarek.gymz.shared.security.Jwt
import com.szastarek.gymz.shared.validation.ValidationError
import com.trendyol.kediatr.Query
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

typealias UserInfoQueryResult = Either<Nel<UserInfoQueryError>, UserInfoFoundResult>

data class UserInfoFoundResult(val userInfo: UserInfo)

data class UserInfoQuery(val token: Jwt) : Query<UserInfoQueryResult> {
    companion object {
        operator fun invoke(token: String) = either {
            val jwt = Jwt.fromRawString(token).mapLeft {
                logger.error { "Failed to parse jwt because of: $it" }
                ValidationError(".id_token", "invalid_id_token")
            }.bind()
            UserInfoQuery(jwt)
        }
    }
}

enum class UserInfoQueryError {
    InvalidJwt,
}
