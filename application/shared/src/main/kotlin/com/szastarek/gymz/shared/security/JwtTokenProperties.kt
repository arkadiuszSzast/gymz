package com.szastarek.gymz.shared.security

interface JwtTokenProperties {
    val audience: JwtAudience
    val issuer: JwtIssuer
    val realm: JwtRealm
    val secret: MaskedString
}
