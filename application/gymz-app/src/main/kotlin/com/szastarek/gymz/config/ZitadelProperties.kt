package com.szastarek.gymz.config

import com.szastarek.gymz.shared.security.ClientId
import com.szastarek.gymz.shared.security.MaskedString
import io.ktor.http.Url

data class ZitadelProperties(
    val baseUrl: Url,
    val clientSecret: MaskedString,
    val clientId: ClientId,
)
