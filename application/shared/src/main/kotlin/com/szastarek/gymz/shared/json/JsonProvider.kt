package com.szastarek.gymz.shared.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object JsonProvider {
    @OptIn(ExperimentalSerializationApi::class)
    val instance = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }
}
