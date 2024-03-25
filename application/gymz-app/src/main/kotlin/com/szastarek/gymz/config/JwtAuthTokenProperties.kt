package com.szastarek.gymz.config

import com.szastarek.gymz.shared.config.ConfigKey
import com.szastarek.gymz.shared.config.ConfigMap
import com.szastarek.gymz.shared.security.JwtAudience
import com.szastarek.gymz.shared.security.JwtIssuer
import com.szastarek.gymz.shared.security.JwtRealm
import com.szastarek.gymz.shared.security.MaskedString

data class JwtAuthTokenProperties(
    val audience: JwtAudience,
    val issuer: JwtIssuer,
    val realm: JwtRealm,
    val secret: MaskedString,
) {
    companion object {
        fun create(config: ConfigMap) = JwtAuthTokenProperties(
            audience = JwtAudience(config.getStringProperty(ConfigKey("jwt.authentication.audience"))),
            issuer = JwtIssuer(config.getStringProperty(ConfigKey("jwt.authentication.issuer"))),
            realm = JwtRealm(config.getStringProperty(ConfigKey("jwt.authentication.realm"))),
            secret = MaskedString(config.getStringProperty(ConfigKey("jwt.authentication.secret"))),
        )
    }
}
