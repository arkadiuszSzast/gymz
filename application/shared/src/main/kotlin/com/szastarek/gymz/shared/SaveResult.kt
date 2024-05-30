package com.szastarek.gymz.shared

sealed interface SaveResult {
    data object Ok : SaveResult
    data class UnknownError(val error: Throwable) : SaveResult
}
