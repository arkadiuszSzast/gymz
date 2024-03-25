package com.szastarek.gymz.config

import com.szastarek.gymz.shared.config.ConfigKey
import com.szastarek.gymz.shared.config.ConfigMap
import com.szastarek.gymz.shared.security.JwtAudience
import com.szastarek.gymz.shared.security.JwtIssuer
import com.szastarek.gymz.shared.security.JwtRealm
import com.szastarek.gymz.shared.security.MaskedString

data class JwtIdTokenProperties(
    val audience: JwtAudience,
    val issuer: JwtIssuer,
    val realm: JwtRealm,
    val secret: MaskedString,
) {
    companion object {
        fun create(config: ConfigMap): JwtIdTokenProperties {
            return JwtIdTokenProperties(
                audience = JwtAudience(config.getStringProperty(ConfigKey("jwt.id.audience"))),
                issuer = JwtIssuer(config.getStringProperty(ConfigKey("jwt.id.issuer"))),
                realm = JwtRealm(config.getStringProperty(ConfigKey("jwt.id.realm"))),
                secret = MaskedString(config.getStringProperty(ConfigKey("jwt.id.secret"))),
            )
        }
    }
}
