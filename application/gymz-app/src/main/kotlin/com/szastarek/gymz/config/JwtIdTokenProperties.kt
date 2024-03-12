package com.szastarek.gymz.config

import com.szastarek.gymz.shared.security.JwtAudience
import com.szastarek.gymz.shared.security.JwtIssuer
import com.szastarek.gymz.shared.security.JwtRealm
import com.szastarek.gymz.shared.security.MaskedString

data class JwtIdTokenProperties(
    val audience: JwtAudience,
    val issuer: JwtIssuer,
    val realm: JwtRealm,
    val secret: MaskedString,
)
