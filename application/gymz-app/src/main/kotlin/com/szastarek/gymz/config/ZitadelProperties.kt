package com.szastarek.gymz.config

import com.szastarek.gymz.shared.security.ClientId
import com.szastarek.gymz.shared.security.MaskedString
import io.ktor.http.Url

data class ZitadelProperties(
    val authorizeUrl: Url,
    val accessTokenUrl: Url,
    val callbackUrl: Url,
    val clientSecret: MaskedString,
    val clientId: ClientId,
)
