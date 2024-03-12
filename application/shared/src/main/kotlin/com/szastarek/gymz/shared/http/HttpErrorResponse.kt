package com.szastarek.gymz.shared.http

import com.szastarek.gymz.shared.validation.ValidationError
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

sealed interface HttpErrorResponse {
    val type: String
    val title: String
    val instance: String
    val detail: String?
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ProblemHttpErrorResponse(
    override val type: String,
    override val title: String,
    override val instance: String,
    @EncodeDefault(EncodeDefault.Mode.NEVER) override val detail: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val errors: List<String> = emptyList(),
) : HttpErrorResponse

@Serializable
data class ValidationErrorHttpMessage(
    val validationErrors: List<ValidationError>,
    override val type: String,
    override val instance: String,
) : HttpErrorResponse {
    override val title: String = "Validation did not pass."
    override val detail: String? = null
}
