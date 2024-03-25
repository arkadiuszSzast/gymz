package com.szastarek.gymz.config

import com.szastarek.gymz.shared.config.ConfigKey
import com.szastarek.gymz.shared.config.ConfigMap
import com.szastarek.gymz.shared.security.ClientId
import com.szastarek.gymz.shared.security.MaskedString
import io.ktor.http.Url

data class ZitadelProperties(
    val authorizeUrl: Url,
    val accessTokenUrl: Url,
    val callbackUrl: Url,
    val clientSecret: MaskedString,
    val clientId: ClientId,
) {
    companion object {
        fun create(config: ConfigMap): ZitadelProperties = ZitadelProperties(
            authorizeUrl = Url(config.getStringProperty(ConfigKey("zitadel.authorizeUrl"))),
            accessTokenUrl = Url(config.getStringProperty(ConfigKey("zitadel.accessTokenUrl"))),
            callbackUrl = Url(config.getStringProperty(ConfigKey("zitadel.callbackUrl"))),
            clientSecret = MaskedString(config.getStringProperty(ConfigKey("zitadel.clientSecret"))),
            clientId = ClientId(config.getStringProperty(ConfigKey("zitadel.clientId"))),
        )
    }
}
