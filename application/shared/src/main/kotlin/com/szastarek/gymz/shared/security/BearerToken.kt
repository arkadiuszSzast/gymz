package com.szastarek.gymz.shared.security

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class BearerToken(val value: String)
